package com.amazonaws.examples.deserialize;

import static com.amazonaws.examples.deserialize.JsonUtils.consumeArray;
import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.medialive.model.EncoderSettings;
import software.amazon.awssdk.services.medialive.model.TimecodeConfig;

public class EncoderSettingsDeserializer extends JsonDeserializer<EncoderSettings> {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private AudioDescriptionDeserializer audioDescriptionDeserializer;
  private VideoDescriptionDeserializer videoDescriptionDeserializer;
  private OutputGroupDeserializer outputGroupDeserializer;

  public EncoderSettingsDeserializer() {
    logger.info("Loading...");
    outputGroupDeserializer = new OutputGroupDeserializer();
    audioDescriptionDeserializer = new AudioDescriptionDeserializer();
    videoDescriptionDeserializer = new VideoDescriptionDeserializer();
  }

  @Override public EncoderSettings deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {

    JsonToken token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    EncoderSettings.Builder builder = EncoderSettings.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeOutputGroups(parser, ctxt, token, builder);
      checkAndConsumeAudioDescriptions(parser, ctxt, token, builder);
      checkAndConsumeVideoDescriptions(parser, ctxt, token, builder);
      checkAndConsumeTimecodeConfig(parser, ctxt, token, builder);

      token = parser.nextToken();
    }

    validateTokenType(parser, token, JsonToken.END_OBJECT);

    return builder.build();
  }

  private void checkAndConsumeTimecodeConfig(JsonParser parser, DeserializationContext ctxt,
      JsonToken token, EncoderSettings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "timecodeConfig".equals(parser.getText())) {
      TimecodeConfig timecodeConfig = parser.readValueAs(TimecodeConfig.class);
      builder.timecodeConfig(timecodeConfig);
    }
  }

  private void checkAndConsumeOutputGroups(JsonParser parser, DeserializationContext ctxt,
      JsonToken token,
      EncoderSettings.Builder builder)
      throws IOException {
    if (token == JsonToken.FIELD_NAME && "outputGroups".equals(parser.getText())) {
      builder.outputGroups(consumeArray(parser, ctxt, outputGroupDeserializer));
    }
  }

  private void checkAndConsumeAudioDescriptions(JsonParser parser, DeserializationContext ctxt,
      JsonToken token,
      EncoderSettings.Builder builder)
      throws IOException {
    if (token == JsonToken.FIELD_NAME && "audioDescriptions".equals(parser.getText())) {
      builder.audioDescriptions(consumeArray(parser, ctxt, audioDescriptionDeserializer));
    }
  }

  private void checkAndConsumeVideoDescriptions(JsonParser parser, DeserializationContext ctxt,
      JsonToken token,
      EncoderSettings.Builder builder)
      throws IOException {
    if (token == JsonToken.FIELD_NAME && "videoDescriptions".equals(parser.getText())) {
      builder.videoDescriptions(consumeArray(parser, ctxt, videoDescriptionDeserializer));
    }
  }
}
