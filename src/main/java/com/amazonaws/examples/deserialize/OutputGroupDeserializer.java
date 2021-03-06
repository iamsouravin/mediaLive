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

import software.amazon.awssdk.services.medialive.model.OutputGroup;
import software.amazon.awssdk.services.medialive.model.OutputGroupSettings;

public class OutputGroupDeserializer extends JsonDeserializer<OutputGroup> {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private OutputDeserializer outputDeserializer;

  public OutputGroupDeserializer() {
    logger.info("Loading...");
    outputDeserializer = new OutputDeserializer();
  }

  @Override public OutputGroup deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {
    JsonToken token = parser.getCurrentToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    OutputGroup.Builder builder = OutputGroup.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeOutputGroupSettings(parser, token, builder);
      checkAndConsumeOutputs(parser, ctxt, token, builder);

      token = parser.nextToken();
    }
    return builder.build();
  }

  private void checkAndConsumeOutputGroupSettings(JsonParser parser, JsonToken token,
      OutputGroup.Builder builder)
      throws IOException {
    if (token == JsonToken.FIELD_NAME && "outputGroupSettings".equals(parser.getText())) {
      OutputGroupSettings outputGroupSettings = parser.readValueAs(OutputGroupSettings.class);
      builder.outputGroupSettings(outputGroupSettings);
    }
  }

  private void checkAndConsumeOutputs(JsonParser parser, DeserializationContext ctxt,
      JsonToken token,
      OutputGroup.Builder builder)
      throws IOException {
    if (token == JsonToken.FIELD_NAME && "outputs".equals(parser.getText())) {
      builder.outputs(consumeArray(parser, ctxt, outputDeserializer));
    }
  }
}
