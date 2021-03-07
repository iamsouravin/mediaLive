package com.amazonaws.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.amazonaws.examples.utils.ResourceUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.medialive.MediaLiveClient;
import software.amazon.awssdk.services.medialive.model.ChannelState;
import software.amazon.awssdk.services.medialive.model.ChannelSummary;
import software.amazon.awssdk.services.medialive.model.DeleteChannelRequest;
import software.amazon.awssdk.services.medialive.model.DeleteChannelResponse;
import software.amazon.awssdk.services.medialive.model.DeleteInputRequest;
import software.amazon.awssdk.services.medialive.model.DeleteInputSecurityGroupRequest;
import software.amazon.awssdk.services.medialive.model.DescribeChannelRequest;
import software.amazon.awssdk.services.medialive.model.DescribeChannelResponse;
import software.amazon.awssdk.services.medialive.model.DescribeInputRequest;
import software.amazon.awssdk.services.medialive.model.DescribeInputResponse;
import software.amazon.awssdk.services.medialive.model.InputAttachment;
import software.amazon.awssdk.services.medialive.model.InputSecurityGroup;
import software.amazon.awssdk.services.medialive.model.InputState;
import software.amazon.awssdk.services.medialive.model.ListChannelsRequest;
import software.amazon.awssdk.services.medialive.model.ListChannelsResponse;
import software.amazon.awssdk.services.medialive.model.ListInputSecurityGroupsRequest;
import software.amazon.awssdk.services.medialive.model.ListInputSecurityGroupsResponse;

public class AppIT {

    private static final String CHANNEL_NAME = "MyEML_Channel_1";

    private Logger logger = LoggerFactory.getLogger(getClass());
    private MediaLiveClient emlClient = DependencyFactory.mediaLiveClient();

    @AfterEach
    public void teardown() throws Exception {
        logger.debug("Listing all channels...");
        ListChannelsRequest.Builder listChannelsRequestBuilder = ListChannelsRequest.builder();
        ListChannelsResponse listChannelsResponse = emlClient.listChannels(listChannelsRequestBuilder.build());
        List<ChannelSummary> channelsToDelete = new ArrayList<>();
        String nextToken = null;
        Set<String> inputIds = new HashSet<>();

        logger.debug("Collecting channels matching name: {}", CHANNEL_NAME);
        for (;;) {
            for (ChannelSummary channelSummary : listChannelsResponse.channels()) {
                if (CHANNEL_NAME.equals(channelSummary.name())) {
                    String channelId = channelSummary.id();
                    logger.debug("Found channel id: {}", channelId);
                    
                    for (InputAttachment inputAttachment : channelSummary.inputAttachments()) {
                        inputIds.add(inputAttachment.inputId());
                    }
                    
                    waitTillChannelIsCreated(channelSummary);
                    
                    logger.debug("Adding channel id: {}", channelId);
                    channelsToDelete.add(channelSummary);
                }
            }
            nextToken = listChannelsResponse.nextToken();
            if (Objects.nonNull(nextToken)) {
                listChannelsRequestBuilder.nextToken(nextToken);
                listChannelsResponse = emlClient.listChannels(listChannelsRequestBuilder.build());
                continue;
            }
            break;
        }

        List<InputSecurityGroup> inputSecurityGroupsToDelete = collectInputSecurityGroupsToDelete(inputIds);

        deleteChannelsAndAttachedInputs(channelsToDelete);

        deleteInputSecurityGroups(inputSecurityGroupsToDelete);
    }

    @Test
    public void handleRequest_shouldCreateChannelFromJsonInput() {
        App function = new App();
        InputStream inputStream = ResourceUtils.getInstance().getInputStream("/CreateEmlRtmpToEmpChannelSettings.json");
        Map<String, String> response = function.handleRequest(inputStream, null);
        assertNotNull(response, "Response object should be created.");
        assertNotNull(response.get("id"), "Channel id should be available.");
        assertNotNull(response.get("arn"), "Channel arn should be available.");
        assertEquals(CHANNEL_NAME, response.get("name"), "Channel name must match input.");
        assertNotNull(response.get("state"), "Channel state should be available.");
    }

    private void waitTillChannelIsCreated(ChannelSummary channelSummary) throws InterruptedException {
        String channelId = channelSummary.id();
        DescribeChannelRequest.Builder describeChannelRequestBuilder = DescribeChannelRequest.builder().channelId(channelId);
        for (ChannelState channelState = channelSummary.state(); channelState == ChannelState.CREATING;) {
            logger.debug("Channel creation is in progress...");
            logger.debug("Waiting for 5 seconds for channel creation to complete...");
            Thread.sleep(5000);
            logger.debug("Describing channel id: {}", channelId);
            DescribeChannelResponse describeChannelResponse = emlClient.describeChannel(describeChannelRequestBuilder.build());
            channelState = describeChannelResponse.state();
        }
    }

    private void deleteInputSecurityGroups(List<InputSecurityGroup> inputSecurityGroupsToDelete) {
        logger.debug("Deleting input security groups...");
        DeleteInputSecurityGroupRequest.Builder deleteInputSecurityGroupRequestBuilder = DeleteInputSecurityGroupRequest.builder();
        for (InputSecurityGroup inputSecurityGroup : inputSecurityGroupsToDelete) {
            String inputSecurityGroupId = inputSecurityGroup.id();
            logger.debug("Deleting input security group id: {}", inputSecurityGroupId);
            DeleteInputSecurityGroupRequest deleteInputSecurityGroupRequest = deleteInputSecurityGroupRequestBuilder.inputSecurityGroupId(inputSecurityGroupId).build();
            emlClient.deleteInputSecurityGroup(deleteInputSecurityGroupRequest);
            logger.debug("Deleted input security group id: {}", inputSecurityGroupId);
        }
    }

    private void deleteChannelsAndAttachedInputs(List<ChannelSummary> channelsToDelete) throws InterruptedException {
        logger.debug("Deleting channels...");
        for (ChannelSummary channel : channelsToDelete) {
            String channelId = channel.id();
            logger.debug("Deleting channel id: {}", channelId);
            DeleteChannelRequest.Builder deleteChannelRequestBuilder = DeleteChannelRequest
                .builder()
                .channelId(channelId);
            DeleteChannelResponse deleteChannelResponse = emlClient.deleteChannel(deleteChannelRequestBuilder.build());

            waitTillChannelIsDeleted(channelId, deleteChannelResponse);

            logger.debug("Deleted channel id: {}", channelId);

            List<InputAttachment> inputAttachments = channel.inputAttachments();
            for (InputAttachment inputAttachment : inputAttachments) {
                String inputId = inputAttachment.inputId();
                DeleteInputRequest deleteInputRequest = DeleteInputRequest.builder().inputId(inputId).build();
                emlClient.deleteInput(deleteInputRequest);

                waitTillInputIsDeleted(inputId);

                logger.debug("Deleted input id: {}", inputId);
            }
        }
    }

    private void waitTillInputIsDeleted(String inputId) throws InterruptedException {
        DescribeInputRequest describeInputRequest = DescribeInputRequest.builder().inputId(inputId).build();
        DescribeInputResponse describeInputResponse = emlClient.describeInput(describeInputRequest);
        InputState inputState = describeInputResponse.state();
        logger.debug("Input id: {}, state: {}", inputId, inputState);
        for (; inputState == InputState.DELETING;) {
            logger.debug("Input deletion is in progress...");
            logger.debug("Waiting for 5 seconds for input deletion to complete...");
            Thread.sleep(5000);
            logger.debug("Describing input id: {}", inputId);
            describeInputResponse = emlClient.describeInput(describeInputRequest);
            inputState = describeInputResponse.state();
        }
    }

    private void waitTillChannelIsDeleted(String channelId, DeleteChannelResponse deleteChannelResponse) throws InterruptedException {
        ChannelState channelState = deleteChannelResponse.state();
        logger.debug("Channel id: {}, state: {}", channelId, channelState);
        DescribeChannelRequest.Builder describeChannelRequestBuilder = DescribeChannelRequest.builder();
        for (; channelState == ChannelState.DELETING;) {
            logger.debug("Channel deletion is in progress...");
            logger.debug("Waiting for 5 seconds for channel deletion to complete...");
            Thread.sleep(5000);
            logger.debug("Describing channel id: {}", channelId);
            describeChannelRequestBuilder.channelId(channelId);
            DescribeChannelResponse describeChannelResponse = emlClient.describeChannel(describeChannelRequestBuilder.build());
            channelState = describeChannelResponse.state();
        }
    }

    private List<InputSecurityGroup> collectInputSecurityGroupsToDelete(Set<String> inputIds) {
        logger.debug("Collecting input security groups to delete...");
        ListInputSecurityGroupsRequest.Builder listInputSecurityGroupsRequestBuilder = ListInputSecurityGroupsRequest.builder();
        ListInputSecurityGroupsResponse listInputSecurityGroupsResponse = emlClient.listInputSecurityGroups(listInputSecurityGroupsRequestBuilder.build());
        List<InputSecurityGroup> inputSecurityGroupsToDelete = new ArrayList<>();
        for (;;) {
            for (InputSecurityGroup inputSecurityGroup : listInputSecurityGroupsResponse.inputSecurityGroups()) {
                Set<String> inputSecurityGroupInputs = new HashSet<>(inputSecurityGroup.inputs());
                if (inputIds.containsAll(inputSecurityGroupInputs)) {
                    inputSecurityGroupsToDelete.add(inputSecurityGroup);
                }
            }
            String nextToken = listInputSecurityGroupsResponse.nextToken();
            if (Objects.nonNull(nextToken)) {
                listInputSecurityGroupsRequestBuilder.nextToken(nextToken);
                listInputSecurityGroupsResponse = emlClient.listInputSecurityGroups(listInputSecurityGroupsRequestBuilder.build());
                continue;
            }
            break;
        }
        return inputSecurityGroupsToDelete;
    }
}
