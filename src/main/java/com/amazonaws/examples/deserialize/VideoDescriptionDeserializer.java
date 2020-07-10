package com.amazonaws.examples.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.medialive.model.VideoCodecSettings;
import software.amazon.awssdk.services.medialive.model.VideoDescription;
import software.amazon.awssdk.services.medialive.model.VideoDescriptionScalingBehavior;

import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

public class VideoDescriptionDeserializer extends JsonDeserializer<VideoDescription> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public VideoDescriptionDeserializer() {
    logger.info("Loading...");
  }

  @Override public VideoDescription deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {

    JsonToken token = parser.getCurrentToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    VideoDescription.Builder builder = VideoDescription.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeHeight(parser, token, builder);
      checkAndConsumeName(parser, token, builder);
      checkAndConsumeScalingBehavior(parser, token, builder);
      checkAndConsumeWidth(parser, token, builder);
      checkAndConsumeCodecSettings(parser, token, builder);

      token = parser.nextToken();
    }
    return builder.build();
  }

  private void checkAndConsumeCodecSettings(JsonParser parser, JsonToken token,
      VideoDescription.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "codecSettings".equals(parser.getText())) {
      VideoCodecSettings codecSettings = parser.readValueAs(VideoCodecSettings.class);

      builder.codecSettings(codecSettings);
    }
  }

  private void checkAndConsumeWidth(JsonParser parser, JsonToken token,
      VideoDescription.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "width".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_NUMBER_INT);

      builder.width(parser.getValueAsInt());
    }
  }

  private void checkAndConsumeScalingBehavior(JsonParser parser,
      JsonToken token, VideoDescription.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "scalingBehavior".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.scalingBehavior(
          VideoDescriptionScalingBehavior.fromValue(parser.getValueAsString("DEFAULT")));
    }
  }

  private void checkAndConsumeHeight(JsonParser parser, JsonToken token,
      VideoDescription.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "height".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_NUMBER_INT);

      builder.height(parser.getValueAsInt());
    }
  }

  private void checkAndConsumeName(JsonParser parser, JsonToken token,
      VideoDescription.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "name".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.name(parser.getValueAsString());
    }
  }
}
