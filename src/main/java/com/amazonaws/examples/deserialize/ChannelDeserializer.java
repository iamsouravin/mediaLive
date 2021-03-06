package com.amazonaws.examples.deserialize;

import static com.amazonaws.examples.deserialize.JsonUtils.consumeArray;
import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.medialive.model.Channel;
import software.amazon.awssdk.services.medialive.model.ChannelClass;
import software.amazon.awssdk.services.medialive.model.EncoderSettings;
import software.amazon.awssdk.services.medialive.model.InputAttachment;
import software.amazon.awssdk.services.medialive.model.InputSpecification;
import software.amazon.awssdk.services.medialive.model.LogLevel;
import software.amazon.awssdk.services.medialive.model.OutputDestination;

public class ChannelDeserializer extends JsonDeserializer<Channel> {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private InputAttachmentDeserializer inputAttachmentDeserializer;
  private OutputDestinationDeserializer outputDestinationDeserializer;
  private PipelineDetailDeserializer pipelineDetailDeserializer;

  public ChannelDeserializer() {
    logger.info("Loading...");
    inputAttachmentDeserializer = new InputAttachmentDeserializer();
    outputDestinationDeserializer = new OutputDestinationDeserializer();
    pipelineDetailDeserializer = new PipelineDetailDeserializer();
  }

  @Override public Channel deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {

    JsonToken token = parser.getCurrentToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    Channel.Builder builder = Channel.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeName(parser, token, builder);
      checkAndConsumeInputAttachments(parser, ctxt, token, builder);
      checkAndConsumeDestinations(parser, ctxt, token, builder);
      checkAndConsumeEncoderSettings(parser, token, builder);
      checkAndConsumeRoleArn(parser, token, builder);
      checkAndConsumeInputSpecification(parser, token, builder);
      checkAndConsumeLogLevel(parser, token, builder);
      checkAndConsumeTags(parser, token, builder);
      checkAndConsumeChannelClass(parser, token, builder);
      checkAndConsumePipelineDetails(parser, ctxt, token, builder);

      token = parser.nextToken();
    }

    return builder.build();
  }

  private void checkAndConsumePipelineDetails(JsonParser parser, DeserializationContext ctxt,
      JsonToken token,
      Channel.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "pipelineDetails".equals(parser.getText())) {
      builder.pipelineDetails(consumeArray(parser, ctxt, pipelineDetailDeserializer));
    }
  }

  private void checkAndConsumeChannelClass(JsonParser parser, JsonToken token,
      Channel.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "channelClass".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.channelClass(ChannelClass.fromValue(parser.getValueAsString()));
    }
  }

  private void checkAndConsumeTags(JsonParser parser, JsonToken token, Channel.Builder builder)
      throws IOException {

    if (token == JsonToken.FIELD_NAME && "tags".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.START_OBJECT);

      Map<String, String> tags = new HashMap<>();
      token = parser.nextToken();
      if (token == JsonToken.FIELD_NAME) {
        String key = parser.getText();

        token = parser.nextToken();
        validateTokenType(parser, token, JsonToken.VALUE_STRING);
        String value = parser.getValueAsString();

        tags.put(key, value);
      } else if (token == JsonToken.END_OBJECT) {
        builder.tags(tags);
      }
    }
  }

  private void checkAndConsumeLogLevel(JsonParser parser, JsonToken token,
      Channel.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "logLevel".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.logLevel(LogLevel.fromValue(parser.getValueAsString()));
    }
  }

  private void checkAndConsumeInputSpecification(JsonParser parser, JsonToken token,
      Channel.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "inputSpecification".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.START_OBJECT);

      InputSpecification inputSpecification = parser.readValueAs(InputSpecification.class);

      builder.inputSpecification(inputSpecification);
    }
  }

  private void checkAndConsumeRoleArn(JsonParser parser, JsonToken token, Channel.Builder builder)
      throws IOException {

    if (token == JsonToken.FIELD_NAME && "roleArn".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.roleArn(parser.getValueAsString());
    }
  }

  private void checkAndConsumeEncoderSettings(JsonParser parser, JsonToken token,
      Channel.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "encoderSettings".equals(parser.getText())) {
      EncoderSettings encoderSettings = parser.readValueAs(EncoderSettings.class);

      builder.encoderSettings(encoderSettings);
    }
  }

  private void checkAndConsumeDestinations(JsonParser parser, DeserializationContext ctxt,
      JsonToken token,
      Channel.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "destinations".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.START_ARRAY);

      List<OutputDestination> outputDestinations = new ArrayList<>();
      token = parser.nextToken();
      while (token != JsonToken.END_ARRAY) {
        OutputDestination outputDestination =
            outputDestinationDeserializer.deserialize(parser, ctxt);
        outputDestinations.add(outputDestination);

        token = parser.nextToken();
      }

      builder.destinations(outputDestinations);
    }
  }

  private void checkAndConsumeInputAttachments(JsonParser parser, DeserializationContext ctxt,
      JsonToken token,
      Channel.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "inputAttachments".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.START_ARRAY);

      List<InputAttachment> inputAttachments = new ArrayList<>();
      token = parser.nextToken();
      while (token != JsonToken.END_ARRAY) {
        InputAttachment inputAttachment = inputAttachmentDeserializer.deserialize(parser, ctxt);
        inputAttachments.add(inputAttachment);

        token = parser.nextToken();
      }

      builder.inputAttachments(inputAttachments);
    }
  }

  private void checkAndConsumeName(JsonParser parser, JsonToken token, Channel.Builder builder)
      throws IOException {

    if (token == JsonToken.FIELD_NAME && "name".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.name(parser.getValueAsString());
    }
  }
}
