package com.amazonaws.examples.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.medialive.model.InputCodec;
import software.amazon.awssdk.services.medialive.model.InputMaximumBitrate;
import software.amazon.awssdk.services.medialive.model.InputResolution;
import software.amazon.awssdk.services.medialive.model.InputSpecification;

import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

public class InputSpecificationDeserializer extends JsonDeserializer<InputSpecification> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public InputSpecificationDeserializer() {
    logger.info("Loading...");
  }

  @Override public InputSpecification deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {

    JsonToken token = parser.getCurrentToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    InputSpecification.Builder builder = InputSpecification.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeCodec(parser, token, builder);
      checkAndConsumeResolution(parser, token, builder);
      checkAndConsumeMaximumBitrate(parser, token, builder);

      token = parser.nextToken();
    }

    return builder.build();
  }

  private void checkAndConsumeMaximumBitrate(JsonParser parser, JsonToken token,
      InputSpecification.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "maximumBitrate".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.maximumBitrate(InputMaximumBitrate.fromValue(parser.getValueAsString()));
    }
  }

  private void checkAndConsumeResolution(JsonParser parser, JsonToken token,
      InputSpecification.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "resolution".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.resolution(InputResolution.fromValue(parser.getValueAsString()));
    }
  }

  private void checkAndConsumeCodec(JsonParser parser, JsonToken token,
      InputSpecification.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "codec".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.codec(InputCodec.fromValue(parser.getValueAsString()));
    }
  }
}
