package com.amazonaws.examples.utils;

import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceUtils {
  private static final ResourceUtils INSTANCE = new ResourceUtils();
  private static final int MAX_SIZE = 1024;

  private final Logger logger = LoggerFactory.getLogger(ResourceUtils.class);

  private ResourceUtils() {

  }

  public static ResourceUtils getInstance() {
    return INSTANCE;
  }

  public String loadResource(String name) {
    logger.info("Loading resource: {}", name);
    try (InputStream in = getClass().getResourceAsStream(name)) {
      char[] chBuff = new char[MAX_SIZE];
      InputStreamReader reader = new InputStreamReader(in);
      CharArrayWriter writer = new CharArrayWriter();
      for (int c; (c = reader.read(chBuff, 0, MAX_SIZE)) != -1; ) {
        writer.write(chBuff, 0, c);
      }
      return writer.toString();
    } catch (Exception e) {
      throw new RuntimeException(
          "Caught an exception while trying to load resource '" + name + "'", e);
    }
  }

  public String getEnv(String name, String def) {
    String val;
    if ((Objects.isNull(name) || name.isEmpty())) {
      if (Objects.isNull(def)) return null;
      val = def;
    } else {
      val = System.getenv(name);
      if (Objects.isNull(val) || val.isEmpty()) {
        if (Objects.isNull(def)) return null;
        val = def;
      }
    }
    return val;
  }

  private final String[] EMPTY_STRING_ARRAY = {};
  public String[] getEnvAsArray(String name, String def) {
    String val;
    if ((Objects.isNull(name) || name.isEmpty())) {
      if (Objects.isNull(def)) return EMPTY_STRING_ARRAY;
      val = def;
    } else {
      val = System.getenv(name);
      if (Objects.isNull(val) || val.isEmpty()) {
        if (Objects.isNull(def)) return EMPTY_STRING_ARRAY;
        val = def;
      }
    }
    String[] splits = val.split("\\s*,\\s*");
    List<String> vals = new ArrayList<>();
    for (String split : splits) {
      String trimmed = split.trim();
      if (trimmed.length() == 0) continue;
      vals.add(trimmed);
    }

    return vals.toArray(EMPTY_STRING_ARRAY);
  }
}
