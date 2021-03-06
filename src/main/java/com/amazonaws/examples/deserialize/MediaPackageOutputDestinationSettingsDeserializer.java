package com.amazonaws.examples.deserialize;

import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.medialive.model.MediaPackageOutputDestinationSettings;

public class MediaPackageOutputDestinationSettingsDeserializer extends
    JsonDeserializer<MediaPackageOutputDestinationSettings> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public MediaPackageOutputDestinationSettingsDeserializer() {
    logger.info("Loading...");
  }

  @Override public MediaPackageOutputDestinationSettings deserialize(JsonParser parser,
      DeserializationContext ctxt) throws IOException {

    JsonToken token = parser.getCurrentToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    MediaPackageOutputDestinationSettings.Builder builder =
        MediaPackageOutputDestinationSettings.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeChannelId(parser, token, builder);

      token = parser.nextToken();
    }

    return builder.build();
  }

  private void checkAndConsumeChannelId(JsonParser parser, JsonToken token,
      MediaPackageOutputDestinationSettings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "channelId".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.channelId(parser.getValueAsString());
    }
  }
}
