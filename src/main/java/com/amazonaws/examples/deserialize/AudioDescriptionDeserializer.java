package com.amazonaws.examples.deserialize;

import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.medialive.model.AudioCodecSettings;
import software.amazon.awssdk.services.medialive.model.AudioDescription;

public class AudioDescriptionDeserializer extends JsonDeserializer<AudioDescription> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public AudioDescriptionDeserializer() {
    logger.info("Loading...");
  }

  @Override public AudioDescription deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {

    JsonToken token = parser.currentToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    AudioDescription.Builder builder = AudioDescription.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeAudioSelectorName(parser, token, builder);
      checkAndConsumeAudioTypeControl(parser, token, builder);
      checkAndConsumeLanguageCodeControl(parser, token, builder);
      checkAndConsumeName(parser, token, builder);
      checkAndConsumeCodecSettings(parser, token, builder);

      token = parser.nextToken();
    }

    validateTokenType(parser, token, JsonToken.END_OBJECT);

    return builder.build();
  }

  private void checkAndConsumeCodecSettings(JsonParser parser, JsonToken token,
      AudioDescription.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "codecSettings".equals(parser.getText())) {
      AudioCodecSettings codecSettings = parser.readValueAs(AudioCodecSettings.class);

      builder.codecSettings(codecSettings);
    }
  }

  private void checkAndConsumeName(JsonParser parser, JsonToken token,
      AudioDescription.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "name".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.name(parser.getText());
    }
  }

  private void checkAndConsumeLanguageCodeControl(JsonParser parser, JsonToken token,
      AudioDescription.Builder builder)
      throws IOException {

    if (token == JsonToken.FIELD_NAME && "languageCodeControl".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.audioTypeControl(parser.getText());
    }
  }

  private void checkAndConsumeAudioTypeControl(JsonParser parser, JsonToken token,
      AudioDescription.Builder builder)
      throws IOException {

    if (token == JsonToken.FIELD_NAME && "audioTypeControl".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.audioTypeControl(parser.getText());
    }
  }

  private void checkAndConsumeAudioSelectorName(JsonParser parser, JsonToken token,
      AudioDescription.Builder builder)
      throws IOException {

    if (token == JsonToken.FIELD_NAME && "audioSelectorName".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.audioSelectorName(parser.getText());
    }
  }
}
