package com.amazonaws.examples.deserialize;

import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.medialive.model.InputAttachment;
import software.amazon.awssdk.services.medialive.model.InputSettings;

public class InputAttachmentDeserializer extends JsonDeserializer<InputAttachment> {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private InputSettingsDeserializer inputSettingsDeserializer;

  public InputAttachmentDeserializer() {
    logger.info("Loading...");
    inputSettingsDeserializer = new InputSettingsDeserializer();
  }

  @Override public InputAttachment deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {

    JsonToken token = parser.getCurrentToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    InputAttachment.Builder builder = InputAttachment.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeInputId(parser, token, builder);
      checkAndConsumeInputAttachmentName(parser, token, builder);
      checkAndConsumeInputSettings(parser, ctxt, token, builder);

      token = parser.nextToken();
    }

    return builder.build();
  }

  private void checkAndConsumeInputSettings(JsonParser parser, DeserializationContext ctxt,
      JsonToken token,
      InputAttachment.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "inputSettings".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.START_OBJECT);

      InputSettings inputSettings = inputSettingsDeserializer.deserialize(parser, ctxt);

      builder.inputSettings(inputSettings);
    }
  }

  private void checkAndConsumeInputAttachmentName(JsonParser parser, JsonToken token,
      InputAttachment.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "inputAttachmentName".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.inputAttachmentName(parser.getValueAsString());
    }
  }

  private void checkAndConsumeInputId(JsonParser parser, JsonToken token,
      InputAttachment.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "inputId".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.inputId(parser.getValueAsString());
    }
  }
}
