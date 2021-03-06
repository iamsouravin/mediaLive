package com.amazonaws.examples.deserialize;

import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.medialive.model.H264Settings;
import software.amazon.awssdk.services.medialive.model.VideoCodecSettings;

public class VideoCodecSettingsDeserializer extends JsonDeserializer<VideoCodecSettings> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public VideoCodecSettingsDeserializer() {
    logger.info("Loading...");
  }

  @Override public VideoCodecSettings deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {

    JsonToken token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    VideoCodecSettings.Builder builder = VideoCodecSettings.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeAacSettings(parser, token, builder);

      token = parser.nextToken();
    }

    validateTokenType(parser, token, JsonToken.END_OBJECT);

    return builder.build();
  }

  private void checkAndConsumeAacSettings(JsonParser parser, JsonToken token,
      VideoCodecSettings.Builder builder) throws IOException {
    if (token == JsonToken.FIELD_NAME && "h264Settings".equals(parser.getText())) {
      H264Settings h264Settings = parser.readValueAs(H264Settings.class);

      builder.h264Settings(h264Settings);
    }
  }
}
