package com.amazonaws.examples.deserialize;

import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.medialive.model.MediaPackageGroupSettings;
import software.amazon.awssdk.services.medialive.model.OutputGroupSettings;

public class OutputGroupSettingsDeserializer extends JsonDeserializer<OutputGroupSettings> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public OutputGroupSettingsDeserializer() {
    logger.info("Loading...");
  }

  @Override public OutputGroupSettings deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {
    JsonToken token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    OutputGroupSettings.Builder builder = OutputGroupSettings.builder();

    token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.FIELD_NAME);

    if ("mediaPackageGroupSettings".equals(parser.getText())) {
      builder.mediaPackageGroupSettings(parser.readValueAs(MediaPackageGroupSettings.class));
    }

    token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.END_OBJECT);

    return builder.build();
  }
}
