package com.amazonaws.examples;

import com.amazonaws.examples.deserialize.AacSettingsDeserializer;
import com.amazonaws.examples.deserialize.AudioCodecSettingsDeserializer;
import com.amazonaws.examples.deserialize.ChannelDeserializer;
import com.amazonaws.examples.deserialize.EncoderSettingsDeserializer;
import com.amazonaws.examples.deserialize.H264SettingsDeserializer;
import com.amazonaws.examples.deserialize.InputSpecificationDeserializer;
import com.amazonaws.examples.deserialize.MediaPackageGroupSettingsDeserializer;
import com.amazonaws.examples.deserialize.MediaPackageOutputSettingsDeserializer;
import com.amazonaws.examples.deserialize.OutputGroupSettingsDeserializer;
import com.amazonaws.examples.deserialize.OutputSettingsDeserializer;
import com.amazonaws.examples.deserialize.TimecodeConfigDeserializer;
import com.amazonaws.examples.deserialize.VideoCodecSettingsDeserializer;
import com.amazonaws.examples.utils.ResourceUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.CreateRoleResponse;
import software.amazon.awssdk.services.iam.model.GetRoleResponse;
import software.amazon.awssdk.services.iam.model.NoSuchEntityException;
import software.amazon.awssdk.services.iam.model.Role;
import software.amazon.awssdk.services.medialive.MediaLiveClient;
import software.amazon.awssdk.services.medialive.model.AacSettings;
import software.amazon.awssdk.services.medialive.model.AudioCodecSettings;
import software.amazon.awssdk.services.medialive.model.Channel;
import software.amazon.awssdk.services.medialive.model.CreateChannelRequest;
import software.amazon.awssdk.services.medialive.model.CreateChannelResponse;
import software.amazon.awssdk.services.medialive.model.CreateInputResponse;
import software.amazon.awssdk.services.medialive.model.CreateInputSecurityGroupResponse;
import software.amazon.awssdk.services.medialive.model.EncoderSettings;
import software.amazon.awssdk.services.medialive.model.H264Settings;
import software.amazon.awssdk.services.medialive.model.Input;
import software.amazon.awssdk.services.medialive.model.InputAttachment;
import software.amazon.awssdk.services.medialive.model.InputDeblockFilter;
import software.amazon.awssdk.services.medialive.model.InputDenoiseFilter;
import software.amazon.awssdk.services.medialive.model.InputDestinationRequest;
import software.amazon.awssdk.services.medialive.model.InputFilter;
import software.amazon.awssdk.services.medialive.model.InputSecurityGroup;
import software.amazon.awssdk.services.medialive.model.InputSourceEndBehavior;
import software.amazon.awssdk.services.medialive.model.InputSpecification;
import software.amazon.awssdk.services.medialive.model.InputType;
import software.amazon.awssdk.services.medialive.model.InputWhitelistRuleCidr;
import software.amazon.awssdk.services.medialive.model.MediaPackageGroupSettings;
import software.amazon.awssdk.services.medialive.model.MediaPackageOutputSettings;
import software.amazon.awssdk.services.medialive.model.OutputGroupSettings;
import software.amazon.awssdk.services.medialive.model.OutputSettings;
import software.amazon.awssdk.services.medialive.model.Smpte2038DataPreference;
import software.amazon.awssdk.services.medialive.model.TimecodeConfig;
import software.amazon.awssdk.services.medialive.model.VideoCodecSettings;

public class ElementalMediaLiveProcessor {
  public static final String MEDIA_LIVE_ACCESS_ROLE = "MediaLiveAccessRole";
  public static final String AMAZON_SSM_READ_ONLY_ACCESS_POLICY_ARN =
      "arn:aws:iam::aws:policy/AmazonSSMReadOnlyAccess";
  public static final String MEDIA_LIVE_CUSTOM_POLICY = "MediaLiveCustomPolicy";
  public static final String REQUEST_ID_PREFIX = "request-";
  private final String TRUST_DOCUMENT =
      ResourceUtils.getInstance().loadResource("/IamRoleTrustDocument.json");
  private final String POLICY_DOCUMENT =
      ResourceUtils.getInstance().loadResource("/IamRoleInlinePolicyDocument.json");
  private final String[] DEFAULT_INPUT_SG_WHITELIST_CIDR =
      ResourceUtils.getInstance()
          .getEnvAsArray("DEFAULT_INPUT_SG_WHITELIST_CIDR", "0.0.0.0/0");
  private final String DEFAULT_RTMP_INPUT_NAME =
      ResourceUtils.getInstance().getEnv("DEFAULT_RTMP_INPUT_NAME", "Default_RTMP_Input");

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final MediaLiveClient emlClient;
  private final IamClient iamClient;

  private final ObjectMapper mapper;

  public ElementalMediaLiveProcessor() {
    emlClient = DependencyFactory.mediaLiveClient();
    iamClient = DependencyFactory.iamClient();

    mapper = initializeMapper();
  }

  private ObjectMapper initializeMapper() {
    SimpleModule module = new SimpleModule()
        .addDeserializer(Channel.class, new ChannelDeserializer())
        .addDeserializer(EncoderSettings.class, new EncoderSettingsDeserializer())
        .addDeserializer(OutputGroupSettings.class, new OutputGroupSettingsDeserializer())
        .addDeserializer(
            MediaPackageGroupSettings.class, new MediaPackageGroupSettingsDeserializer())
        .addDeserializer(OutputSettings.class, new OutputSettingsDeserializer())
        .addDeserializer(
            MediaPackageOutputSettings.class, new MediaPackageOutputSettingsDeserializer())
        .addDeserializer(AudioCodecSettings.class, new AudioCodecSettingsDeserializer())
        .addDeserializer(AacSettings.class, new AacSettingsDeserializer())
        .addDeserializer(VideoCodecSettings.class, new VideoCodecSettingsDeserializer())
        .addDeserializer(H264Settings.class, new H264SettingsDeserializer())
        .addDeserializer(InputSpecification.class, new InputSpecificationDeserializer())
        .addDeserializer(TimecodeConfig.class, new TimecodeConfigDeserializer());

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(module);

    return mapper;
  }

  public Channel parseChannel(String input) throws JsonProcessingException {
    logger.info("Parsing channel JSON payload...");
    return mapper.readValue(input, Channel.class);
  }

  public Channel createChannel(String input) throws JsonProcessingException {
    return createChannel(parseChannel(input));
  }

  public Channel createChannel(Channel channel) {
    logger.info("Creating channel '{}'...", channel.name());
    CreateChannelRequest.Builder builder = CreateChannelRequest
        .builder()
        .name(channel.name())
        .inputAttachments(channel.inputAttachments())
        .destinations(channel.destinations())
        .encoderSettings(channel.encoderSettings())
        .inputSpecification(channel.inputSpecification())
        .logLevel(channel.logLevel())
        .tags(channel.tags())
        .channelClass(channel.channelClass());

    checkRoleArn(channel, builder);

    checkInputAttachments(channel, builder);

    CreateChannelRequest createChannelRequest = builder.build();

    CreateChannelResponse createChannelResponse = emlClient.createChannel(createChannelRequest);
    logger.info("New channel created.");
    logger.info("Channel ID: {}", createChannelResponse.channel().id());
    logger.info("Channel ARN: {}", createChannelResponse.channel().arn());
    logger.info("Channel Name: {}", createChannelResponse.channel().name());
    logger.info("Channel State: {}", createChannelResponse.channel().stateAsString());
    return createChannelResponse.channel();
  }

  private void checkInputAttachments(Channel channel, CreateChannelRequest.Builder builder) {
    List<InputAttachment> inputAttachments;
    if (channel.hasInputAttachments()) {
      inputAttachments = channel.inputAttachments();
    } else {
      inputAttachments = createDefaultInputAttachments();
    }
    builder.inputAttachments(inputAttachments);
  }

  private List<InputAttachment> createDefaultInputAttachments() {
    List<InputAttachment> inputAttachments;
    InputSecurityGroup inSg = createInputSecurityGroup(DEFAULT_INPUT_SG_WHITELIST_CIDR);
    Input input = createRtmpInput(DEFAULT_RTMP_INPUT_NAME, DEFAULT_RTMP_INPUT_NAME, inSg);
    inputAttachments = new ArrayList<>();
    InputAttachment.Builder inputAttachmentBuilder = InputAttachment.builder()
        .inputId(input.id())
        .inputAttachmentName(input.name())
        .inputSettings(inputSettingsBuilder -> inputSettingsBuilder
            .sourceEndBehavior(InputSourceEndBehavior.CONTINUE)
            .inputFilter(InputFilter.AUTO)
            .filterStrength(1)
            .deblockFilter(InputDeblockFilter.DISABLED)
            .denoiseFilter(InputDenoiseFilter.DISABLED)
            .smpte2038DataPreference(Smpte2038DataPreference.IGNORE));
    inputAttachments.add(inputAttachmentBuilder.build());
    return inputAttachments;
  }

  /**
   * If "roleArn" is provided on Channel JSON input then assign the provided arn to channel. Else If
   * "MediaLiveAccessRole" is already defined then assign it to channel Else create a new role and
   * assign it to channel
   *
   * @param channel channel object parsed from input
   * @param builder instance of {@link CreateChannelRequest.Builder}
   */
  private void checkRoleArn(Channel channel, CreateChannelRequest.Builder builder) {

    if (Objects.nonNull(channel.roleArn())) {
      builder.roleArn(channel.roleArn());
    } else {
      Role emlRole = getOrCreateRole(MEDIA_LIVE_ACCESS_ROLE);
      builder.roleArn(emlRole.arn());
    }
  }

  public Input createRtmpInput(String inputName, String inputDestinationStreamName,
      InputSecurityGroup inSg) {
    logger.info("Creating RTMP Input with name '{}'...", inputName);
    final String requestId = REQUEST_ID_PREFIX + UUID.randomUUID().toString();
    CreateInputResponse createInputResponse = emlClient.createInput(builder -> {
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
    Input input = createInputResponse.input();
    logger.info("Created Input - Request Id: {}, Id: {}", requestId, input.id());
    return input;
  }

  public InputSecurityGroup createInputSecurityGroup(String... inWhitelistRuleCidrs) {
    logger.info("Creating Input Security Group...");
    List<InputWhitelistRuleCidr> inWhitelistRuleCidrsList = new ArrayList<>();
    for (String inWhitelistRuleCidr : inWhitelistRuleCidrs) {
      logger.info("Adding Whitelist Rule CIDR: {}", inWhitelistRuleCidr);
      inWhitelistRuleCidrsList.add(
          InputWhitelistRuleCidr.builder().cidr(inWhitelistRuleCidr).build());
    }

    CreateInputSecurityGroupResponse createInputSecurityGroupResponse =
        emlClient.createInputSecurityGroup(
            builder -> builder.whitelistRules(inWhitelistRuleCidrsList));
    InputSecurityGroup inSg = createInputSecurityGroupResponse.securityGroup();
    logger.info("Created Security Group - Id: {}", inSg.id());
    return inSg;
  }

  public Role getOrCreateRole(String roleName) {
    Role emlIamRole;
    try {
      logger.info("Checking for existing role with name '{}'...", roleName);
      GetRoleResponse getIamRoleResp = iamClient.getRole(builder -> builder
          .roleName(roleName));
      logger.info("Role found with name '{}'.", roleName);
      emlIamRole = getIamRoleResp.role();
    } catch (NoSuchEntityException e) {
      logger.error("No Role found with name '{}'", roleName);

      logger.info("Creating new Role with name '{}'", roleName);
      CreateRoleResponse createRoleResp = iamClient.createRole(builder -> builder
          .assumeRolePolicyDocument(TRUST_DOCUMENT)
          .roleName(roleName));

      iamClient.attachRolePolicy(builder -> builder
          .policyArn(AMAZON_SSM_READ_ONLY_ACCESS_POLICY_ARN)
          .roleName(createRoleResp.role().roleName()));

      iamClient.putRolePolicy(builder -> builder
          .policyDocument(POLICY_DOCUMENT)
          .policyName(MEDIA_LIVE_CUSTOM_POLICY)
          .roleName(createRoleResp.role().roleName()));

      emlIamRole = createRoleResp.role();
    }
    logger.info("Role ARN: {}", emlIamRole.arn());
    return emlIamRole;
  }
}
