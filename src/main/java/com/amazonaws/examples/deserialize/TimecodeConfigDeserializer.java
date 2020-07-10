package com.amazonaws.examples.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.medialive.model.TimecodeConfig;
import software.amazon.awssdk.services.medialive.model.TimecodeConfigSource;

import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

public class TimecodeConfigDeserializer extends JsonDeserializer<TimecodeConfig> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public TimecodeConfigDeserializer() {
    logger.info("Loading...");
  }

  @Override public TimecodeConfig deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {

    JsonToken token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    TimecodeConfig.Builder builder = TimecodeConfig.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeSource(parser, ctxt, token, builder);

      token = parser.nextToken();
    }
    validateTokenType(parser, token, JsonToken.END_OBJECT);

    return builder.build();
  }

  private void checkAndConsumeSource(JsonParser parser, DeserializationContext ctxt,
      JsonToken token, TimecodeConfig.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "source".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.source(TimecodeConfigSource.fromValue(parser.getValueAsString()));
    }
  }
}
