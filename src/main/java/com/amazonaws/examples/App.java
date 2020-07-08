package com.amazonaws.examples;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.CreateRoleResponse;
import software.amazon.awssdk.services.iam.model.GetRoleResponse;
import software.amazon.awssdk.services.iam.model.NoSuchEntityException;
import software.amazon.awssdk.services.iam.model.Role;
import software.amazon.awssdk.services.medialive.MediaLiveClient;
import software.amazon.awssdk.services.medialive.model.Channel;
import software.amazon.awssdk.services.medialive.model.ChannelClass;
import software.amazon.awssdk.services.medialive.model.CreateChannelResponse;
import software.amazon.awssdk.services.medialive.model.CreateInputResponse;
import software.amazon.awssdk.services.medialive.model.CreateInputSecurityGroupResponse;
import software.amazon.awssdk.services.medialive.model.Input;
import software.amazon.awssdk.services.medialive.model.InputAttachment;
import software.amazon.awssdk.services.medialive.model.InputCodec;
import software.amazon.awssdk.services.medialive.model.InputDeblockFilter;
import software.amazon.awssdk.services.medialive.model.InputDenoiseFilter;
import software.amazon.awssdk.services.medialive.model.InputDestinationRequest;
import software.amazon.awssdk.services.medialive.model.InputFilter;
import software.amazon.awssdk.services.medialive.model.InputMaximumBitrate;
import software.amazon.awssdk.services.medialive.model.InputResolution;
import software.amazon.awssdk.services.medialive.model.InputSecurityGroup;
import software.amazon.awssdk.services.medialive.model.InputSourceEndBehavior;
import software.amazon.awssdk.services.medialive.model.InputType;
import software.amazon.awssdk.services.medialive.model.InputWhitelistRuleCidr;
import software.amazon.awssdk.services.medialive.model.OutputDestination;
import software.amazon.awssdk.services.medialive.model.Smpte2038DataPreference;
import software.amazon.awssdk.services.s3.S3Client;

import org.slf4j.Logger;

/**
 * Lambda function entry point. You can change to use other pojo type or implement
 * a different RequestHandler.
 *
 * @see <a href=https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html>Lambda Java Handler</a> for more information
 */
public class App implements RequestHandler<Object, Object> {
    public static final String MEDIA_LIVE_ACCESS_ROLE = "MediaLiveAccessRole";
    private final Logger logger = LoggerFactory.getLogger(App.class);

    private final S3Client s3Client;
    private final MediaLiveClient mediaLiveClient;
    private final IamClient iamClient;

    private final String TRUST_DOCUMENT = loadResource("/IamRoleTrustDocument.json");
    private final String POLICY_DOCUMENT = loadResource("/IamRoleInlinePolicyDocument.json");

    public App() {
        // Initialize the SDK client outside of the handler method so that it can be reused for subsequent invocations.
        // It is initialized when the class is loaded.
        s3Client = DependencyFactory.s3Client();
        mediaLiveClient = DependencyFactory.mediaLiveClient();
        iamClient = DependencyFactory.iamClient();
        // Consider invoking a simple api here to pre-warm up the application, eg: dynamodb#listTables
    }

    @Override
    public Object handleRequest(final Object input, final Context context) {

        Role emlIamRole = getOrCreateRole(iamClient);

        InputSecurityGroup inSg = createInputSecurityGroup("0.0.0.0/0");

        Input rtmpInput = createRtmpInput("SouravOBS", "SouravOBS", inSg);

        List<Input> inputs = new ArrayList<>();
        inputs.add(rtmpInput);
        // Channel channel = createChannel("RTMP Push", emlIamRole, inputs);

        return input;
    }

    private Channel createChannel(String name, Role iamRole, List<Input> inputs) {
        logger.info("Creating Channel with name '{}'...", name);
        CreateChannelResponse createChannelResponse = mediaLiveClient.createChannel(builder -> {
            List<OutputDestination> outputDestinations = new ArrayList<>();

            Collection<InputAttachment> inputAttachments = inputs
                .stream()
                .map(input -> createInputAttachment(input))
                .collect(Collectors.toCollection(ArrayList::new));

            builder
                .name(name)
                .roleArn(iamRole.arn())
                .channelClass(ChannelClass.SINGLE_PIPELINE)
                .inputSpecification(inSpecBuilder -> {
                    inSpecBuilder
                        .codec(InputCodec.HEVC)
                        .maximumBitrate(InputMaximumBitrate.MAX_10_MBPS)
                        .resolution(InputResolution.SD);
                })
                .inputAttachments(inputAttachments)
                .destinations(outputDestinations);
        });
        return createChannelResponse.channel();
    }

    private InputAttachment createInputAttachment(Input input) {
        logger.info("Creating Input Attachment...");
        return InputAttachment
            .builder()
            .inputId(input.id())
            .inputSettings(inputSettingsBuilder -> {
                inputSettingsBuilder
                    .inputFilter(InputFilter.AUTO)
                    .filterStrength(1)
                    .deblockFilter(InputDeblockFilter.DISABLED)
                    .denoiseFilter(InputDenoiseFilter.DISABLED)
                    .smpte2038DataPreference(Smpte2038DataPreference.IGNORE)
                    .sourceEndBehavior(InputSourceEndBehavior.CONTINUE);
            })
            .build();
    }

    private Input createRtmpInput(String inputName, String inputDestinationStreamName, InputSecurityGroup inSg) {
        logger.info("Creating RTMP Input with name '{}'...", inputName);
        final String requestId = "request-" + UUID.randomUUID().toString();
        CreateInputResponse createInputResponse = mediaLiveClient.createInput(builder -> {
            List<InputDestinationRequest> inputDestinations = new ArrayList<>();
            inputDestinations.add(InputDestinationRequest
                .builder()
                .streamName(inputDestinationStreamName)
                .build());
            builder
                .name(inputName)
                .requestId(requestId)
                .type(InputType.RTMP_PUSH)
                .destinations(inputDestinations)
                .inputSecurityGroups(inSg.id());
        });
        Input input  = createInputResponse.input();
        logger.info("Created Input - Request Id: {}, Id: {}", requestId, input.id());
        return input;
    }

    private InputSecurityGroup createInputSecurityGroup(String... inWhitelistRuleCidrs) {
        logger.info("Creating Input Security Group...");
        List<InputWhitelistRuleCidr> inWhitelistRuleCidrsList = new ArrayList<>();
        for (String inWhitelistRuleCidr : inWhitelistRuleCidrs) {
            logger.info("Adding Whitelist Rule CIDR: {}", inWhitelistRuleCidr);
            inWhitelistRuleCidrsList.add(InputWhitelistRuleCidr.builder().cidr(inWhitelistRuleCidr).build());
        }

        CreateInputSecurityGroupResponse createInputSecurityGroupResponse = mediaLiveClient.createInputSecurityGroup(builder -> {
            builder.whitelistRules(inWhitelistRuleCidrsList);
        });
        InputSecurityGroup inSg = createInputSecurityGroupResponse.securityGroup();
        logger.info("Created Security Group - Id: {}", inSg.id());
        return inSg;
    }

    private Role getOrCreateRole(IamClient iamClient) {
        Role emlIamRole = null;
        try {
            logger.info("Checking for existing role with name '{}'...", MEDIA_LIVE_ACCESS_ROLE);
            GetRoleResponse getIamRoleResp = iamClient.getRole(builder -> {
                builder
                    .roleName(MEDIA_LIVE_ACCESS_ROLE);
            });
            logger.info("Role found with name '{}'.", MEDIA_LIVE_ACCESS_ROLE);
            emlIamRole = getIamRoleResp.role();
        } catch (NoSuchEntityException e) {
            logger.error("No Role found with name '{}'", MEDIA_LIVE_ACCESS_ROLE);

            logger.info("Creating new Role with name '{}'", MEDIA_LIVE_ACCESS_ROLE);
            CreateRoleResponse createRoleResp = iamClient.createRole(builder -> {
                builder
                    .assumeRolePolicyDocument(TRUST_DOCUMENT)
                    .roleName(MEDIA_LIVE_ACCESS_ROLE);
            });

            iamClient.attachRolePolicy(builder -> {
                builder
                    .policyArn("arn:aws:iam::aws:policy/AmazonSSMReadOnlyAccess")
                    .roleName(createRoleResp.role().roleName());
            });

            iamClient.putRolePolicy(builder -> {
                builder
                    .policyDocument(POLICY_DOCUMENT)
                    .policyName("MediaLiveCustomPolicy")
                    .roleName(createRoleResp.role().roleName());
            });

            emlIamRole = createRoleResp.role();
        }
        logger.info("Role ARN: {}", emlIamRole.arn());
        return emlIamRole;
    }

    private String loadResource(String name) {
        logger.info("Loading resource: {}", name);
        try (InputStream in = getClass().getResourceAsStream(name)) {
            int buffSize = 1024;
            char[] chBuff = new char[buffSize];
            int c = -1;
            InputStreamReader reader = new InputStreamReader(in);
            CharArrayWriter writer = new CharArrayWriter();
            while ((c = reader.read(chBuff, 0, buffSize)) != -1) {
                writer.write(chBuff, 0, c);
            }
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(
                "Caught an exception while trying to load resource '" + name + "'", e);
        }
    }
}
