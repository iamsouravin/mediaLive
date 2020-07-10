package com.amazonaws.examples.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

  public static void validateTokenType(JsonParser parser, JsonToken actual, JsonToken expected)
      throws IOException {
    if (actual != expected) {
      LOGGER.error("Expected Token: {}, Actual Token: {}, Token Value: {}", expected.name(), actual,
          parser.getText());
      throw JsonMappingException.from(parser, "Expected " + expected.name() + ": " + actual);
    }
  }

  public static <T> List<T> consumeArray(JsonParser parser, DeserializationContext ctxt,
      JsonDeserializer<T> deserializer) throws IOException {
    JsonToken token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.START_ARRAY);

    List<T> list = new ArrayList<>();
    token = parser.nextToken();
    while (token != JsonToken.END_ARRAY) {
      T obj = deserializer.deserialize(parser, ctxt);
      list.add(obj);

      token = parser.nextToken();
    }

    return list;
  }
}
