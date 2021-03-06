package com.amazonaws.examples.deserialize;

import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.medialive.model.PipelineDetail;

public class PipelineDetailDeserializer extends JsonDeserializer<PipelineDetail> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public PipelineDetailDeserializer() {
    logger.info("Loading...");
  }

  @Override public PipelineDetail deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {

    JsonToken token = parser.getCurrentToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    PipelineDetail.Builder builder = PipelineDetail.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumePipelineId(parser, token, builder);
      checkAndConsumeActiveInputAttachmentName(parser, token, builder);
      checkAndConsumeActiveInputSwitchActionName(parser, token, builder);

      token = parser.nextToken();
    }

    return builder.build();
  }

  private void checkAndConsumeActiveInputSwitchActionName(JsonParser parser, JsonToken token,
      PipelineDetail.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "activeInputSwitchActionName".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.activeInputSwitchActionName(parser.getValueAsString());
    }
  }

  private void checkAndConsumeActiveInputAttachmentName(JsonParser parser, JsonToken token,
      PipelineDetail.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "activeInputAttachmentName".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.activeInputAttachmentName(parser.getValueAsString());
    }
  }

  private void checkAndConsumePipelineId(JsonParser parser, JsonToken token,
      PipelineDetail.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "pipelineId".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.pipelineId(parser.getValueAsString());
    }
  }
}
