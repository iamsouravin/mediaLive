package com.amazonaws.examples.deserialize;

import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.medialive.model.Output;
import software.amazon.awssdk.services.medialive.model.OutputSettings;

public class OutputDeserializer extends JsonDeserializer<Output> {
  private static final String[] EMPTY_STRING_ARRAY = {};
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public OutputDeserializer() {
    logger.info("Loading...");
  }

  @Override public Output deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {
    JsonToken token = parser.getCurrentToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    Output.Builder builder = Output.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeOutputName(parser, token, builder);
      checkAndConsumeVideoDescriptionName(parser, token, builder);
      checkAndConsumeOutputSettings(parser, token, builder);
      checkAndConsumeAudioDescriptionNames(parser, token, builder);
      checkAndConsumeCaptionDescriptionNames(parser, token, builder);

      token = parser.nextToken();
    }
    return builder.build();
  }

  private void checkAndConsumeCaptionDescriptionNames(JsonParser parser, JsonToken token,
      Output.Builder builder)
      throws IOException {
    if (token == JsonToken.FIELD_NAME && "captionDescriptionNames".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.START_ARRAY);
      String[] captionDescriptionNames = parser.readValueAs(String[].class);

      builder.captionDescriptionNames(captionDescriptionNames);
    }
  }

  private void checkAndConsumeAudioDescriptionNames(JsonParser parser, JsonToken token,
      Output.Builder builder)
      throws IOException {
    if (token == JsonToken.FIELD_NAME && "audioDescriptionNames".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.START_ARRAY);

      List<String> audioDescriptionNames = new ArrayList<>();
      token = parser.nextToken();

      while (token != JsonToken.END_ARRAY) {
        if (token == JsonToken.VALUE_STRING) {
          audioDescriptionNames.add(parser.getText());
        }

        token = parser.nextToken();
      }

      builder.audioDescriptionNames(audioDescriptionNames);
    }
  }

  private void checkAndConsumeOutputSettings(JsonParser parser, JsonToken token,
      Output.Builder builder)
      throws IOException {
    if (token == JsonToken.FIELD_NAME && "outputSettings".equals(parser.getText())) {
      builder.outputSettings(parser.readValueAs(OutputSettings.class));
    }
  }

  private void checkAndConsumeOutputName(JsonParser parser, JsonToken token,
      Output.Builder builder)
      throws IOException {
    if (token == JsonToken.FIELD_NAME && "outputName".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.outputName(parser.getText());
    }
  }

  private void checkAndConsumeVideoDescriptionName(JsonParser parser, JsonToken token,
      Output.Builder builder)
      throws IOException {
    if (token == JsonToken.FIELD_NAME && "videoDescriptionName".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.videoDescriptionName(parser.getText());
    }
  }
}
