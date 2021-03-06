package com.amazonaws.examples.deserialize;

import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.medialive.model.MediaPackageOutputSettings;
import software.amazon.awssdk.services.medialive.model.OutputSettings;

public class OutputSettingsDeserializer extends JsonDeserializer<OutputSettings> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public OutputSettingsDeserializer() {
    logger.info("Loading...");
  }

  @Override public OutputSettings deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {
    JsonToken token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    OutputSettings.Builder builder = OutputSettings.builder();
    token = parser.nextToken();
    if (token == JsonToken.FIELD_NAME && "mediaPackageOutputSettings".equals(parser.getText())) {
      builder.mediaPackageOutputSettings(
          parser.readValueAs(MediaPackageOutputSettings.class));
    }
    token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.END_OBJECT);

    return builder.build();
  }
}
