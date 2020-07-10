package com.amazonaws.examples.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.medialive.model.MediaPackageOutputSettings;

import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

public class MediaPackageOutputSettingsDeserializer
    extends JsonDeserializer<MediaPackageOutputSettings> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public MediaPackageOutputSettingsDeserializer() {
    logger.info("Loading...");
  }

  @Override
  public MediaPackageOutputSettings deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {
    JsonToken token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.END_OBJECT);

    return MediaPackageOutputSettings.builder().build();
  }
}
