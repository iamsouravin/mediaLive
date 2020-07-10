package com.amazonaws.examples.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.medialive.model.AacRawFormat;
import software.amazon.awssdk.services.medialive.model.AacSettings;
import software.amazon.awssdk.services.medialive.model.AacSpec;

import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

public class AacSettingsDeserializer extends JsonDeserializer<AacSettings> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public AacSettingsDeserializer() {
    logger.info("Loading...");
  }

  @Override public AacSettings deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {

    JsonToken token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    AacSettings.Builder builder = AacSettings.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeBitrate(parser, token, builder);
      checkAndConsumeRawFormat(parser, token, builder);
      checkAndConsumeSpec(parser, token, builder);

      token = parser.nextToken();
    }

    validateTokenType(parser, token, JsonToken.END_OBJECT);

    return builder.build();
  }

  private void checkAndConsumeSpec(JsonParser parser, JsonToken token,
      AacSettings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "spec".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.spec(AacSpec.fromValue(parser.getText()));
    }
  }

  private void checkAndConsumeRawFormat(JsonParser parser, JsonToken token,
      AacSettings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "rawFormat".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.rawFormat(AacRawFormat.fromValue(parser.getValueAsString()));
    }
  }

  private void checkAndConsumeBitrate(JsonParser parser, JsonToken token,
      AacSettings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "bitrate".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_NUMBER_INT);

      builder.bitrate(parser.getValueAsDouble());
    }
  }
}
