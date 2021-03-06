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

public class MediaPackageGroupSettingsDeserializer extends
    JsonDeserializer<MediaPackageGroupSettings> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public MediaPackageGroupSettingsDeserializer() {
    logger.info("Loading...");
  }

  @Override
  public MediaPackageGroupSettings deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {

    JsonToken token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    MediaPackageGroupSettings.Builder builder = MediaPackageGroupSettings.builder();

    token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.FIELD_NAME);

    if ("destination".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.START_OBJECT);

      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.FIELD_NAME);

      if ("destinationRefId".equals(parser.getText())) {
        token = parser.nextToken();
        validateTokenType(parser, token, JsonToken.VALUE_STRING);
        String destinationRefId = parser.getText();

        builder.destination(outputLocationRefBuilder -> outputLocationRefBuilder.destinationRefId(
            destinationRefId));
      }

      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.END_OBJECT);
    }

    token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.END_OBJECT);

    return builder.build();
  }
}
