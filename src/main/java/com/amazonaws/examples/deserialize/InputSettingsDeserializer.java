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

import software.amazon.awssdk.services.medialive.model.AudioSelector;
import software.amazon.awssdk.services.medialive.model.CaptionSelector;
import software.amazon.awssdk.services.medialive.model.InputDeblockFilter;
import software.amazon.awssdk.services.medialive.model.InputDenoiseFilter;
import software.amazon.awssdk.services.medialive.model.InputFilter;
import software.amazon.awssdk.services.medialive.model.InputSettings;
import software.amazon.awssdk.services.medialive.model.InputSourceEndBehavior;
import software.amazon.awssdk.services.medialive.model.Smpte2038DataPreference;

public class InputSettingsDeserializer extends JsonDeserializer<InputSettings> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public InputSettingsDeserializer() {
    logger.info("Loading...");
  }

  @Override public InputSettings deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {

    JsonToken token = parser.getCurrentToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    InputSettings.Builder builder = InputSettings.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeSourceEndBehavior(parser, token, builder);
      checkAndConsumeInputFilter(parser, token, builder);
      checkAndConsumeFilterStrength(parser, token, builder);
      checkAndConsumeDeblockFilter(parser, token, builder);
      checkAndConsumeDenoiseFilter(parser, token, builder);
      checkAndConsumeSmpte2038DataPreference(parser, token, builder);
      checkAndConsumeAudioSelectors(parser, token, builder);
      checkAndConsumeCaptionSelectors(parser, token, builder);
      token = parser.nextToken();
    }

    return builder.build();
  }

  private void checkAndConsumeCaptionSelectors(JsonParser parser, JsonToken token,
      InputSettings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "captionSelectors".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.START_ARRAY);

      List<CaptionSelector> captionSelectors = new ArrayList<>();
      token = parser.nextToken();
      while (token != JsonToken.END_ARRAY) {

        token = parser.nextToken();
      }

      builder.captionSelectors(captionSelectors);
    }
  }

  private void checkAndConsumeAudioSelectors(JsonParser parser, JsonToken token,
      InputSettings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "audioSelectors".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.START_ARRAY);

      List<AudioSelector> audioSelectors = new ArrayList<>();
      token = parser.nextToken();
      while (token != JsonToken.END_ARRAY) {

        token = parser.nextToken();
      }

      builder.audioSelectors(audioSelectors);
    }
  }

  private void checkAndConsumeSmpte2038DataPreference(JsonParser parser, JsonToken token,
      InputSettings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "smpte2038DataPreference".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.smpte2038DataPreference(
          Smpte2038DataPreference.fromValue(parser.getValueAsString("IGNORE")));
    }
  }

  private void checkAndConsumeDenoiseFilter(JsonParser parser, JsonToken token,
      InputSettings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "denoiseFilter".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.denoiseFilter(InputDenoiseFilter.fromValue(parser.getValueAsString("DISABLED")));
    }
  }

  private void checkAndConsumeDeblockFilter(JsonParser parser, JsonToken token,
      InputSettings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "deblockFilter".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.deblockFilter(InputDeblockFilter.fromValue(parser.getValueAsString("DISABLED")));
    }
  }

  private void checkAndConsumeFilterStrength(JsonParser parser, JsonToken token,
      InputSettings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "filterStrength".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_NUMBER_INT);

      builder.filterStrength(parser.getValueAsInt(1));
    }
  }

  private void checkAndConsumeInputFilter(JsonParser parser, JsonToken token,
      InputSettings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "inputFilter".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.inputFilter(InputFilter.fromValue(parser.getValueAsString("AUTO")));
    }
  }

  private void checkAndConsumeSourceEndBehavior(JsonParser parser, JsonToken token,
      InputSettings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "sourceEndBehavior".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.sourceEndBehavior(
          InputSourceEndBehavior.fromValue(parser.getValueAsString("CONTINUE")));
    }
  }
}
