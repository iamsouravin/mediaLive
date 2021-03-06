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

import software.amazon.awssdk.services.medialive.model.OutputDestination;

public class OutputDestinationDeserializer extends JsonDeserializer<OutputDestination> {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private MediaPackageOutputDestinationSettingsDeserializer
      mediaPackageOutputDestinationSettingsDeserializer;

  public OutputDestinationDeserializer() {
    logger.info("Loading...");
    mediaPackageOutputDestinationSettingsDeserializer =
        new MediaPackageOutputDestinationSettingsDeserializer();
  }

  @Override public OutputDestination deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {

    JsonToken token = parser.getCurrentToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    OutputDestination.Builder builder = OutputDestination.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeId(parser, token, builder);
      checkAndConsumeMediaPackageSettings(parser, ctxt, token, builder);
      token = parser.nextToken();
    }

    return builder.build();
  }

  private void checkAndConsumeMediaPackageSettings(JsonParser parser, DeserializationContext ctxt,
      JsonToken token,
      OutputDestination.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "mediaPackageSettings".equals(parser.getText())) {
      builder.mediaPackageSettings(
          consumeArray(parser, ctxt, mediaPackageOutputDestinationSettingsDeserializer));
    }
  }

  private void checkAndConsumeId(JsonParser parser, JsonToken token,
      OutputDestination.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "id".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.id(parser.getValueAsString());
    }
  }
}
