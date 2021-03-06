package com.amazonaws.examples.deserialize;

import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.medialive.model.AacSettings;
import software.amazon.awssdk.services.medialive.model.AudioCodecSettings;

public class AudioCodecSettingsDeserializer extends JsonDeserializer<AudioCodecSettings> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public AudioCodecSettingsDeserializer() {
    logger.info("Loading...");
  }

  @Override public AudioCodecSettings deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {
    JsonToken token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    AudioCodecSettings.Builder builder = AudioCodecSettings.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeAacSettings(parser, token, builder);

      token = parser.nextToken();
    }

    validateTokenType(parser, token, JsonToken.END_OBJECT);

    return builder.build();
  }

  private void checkAndConsumeAacSettings(JsonParser parser, JsonToken token,
      AudioCodecSettings.Builder builder) throws IOException {
    if (token == JsonToken.FIELD_NAME && "aacSettings".equals(parser.getText())) {
      AacSettings aacSettings = parser.readValueAs(AacSettings.class);

      builder.aacSettings(aacSettings);
    }
  }
}
