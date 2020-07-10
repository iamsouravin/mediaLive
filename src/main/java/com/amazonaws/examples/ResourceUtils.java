package com.amazonaws.examples;

import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceUtils {
  private static final ResourceUtils INSTANCE = new ResourceUtils();

  private final Logger logger = LoggerFactory.getLogger(ResourceUtils.class);

  private ResourceUtils() {

  }

  public static ResourceUtils getInstance() {
    return INSTANCE;
  }

  public String loadResource(String name) {
    logger.info("Loading resource: {}", name);
    try (InputStream in = getClass().getResourceAsStream(name)) {
      int buffSize = 1024;
      char[] chBuff = new char[buffSize];
      int c;
      InputStreamReader reader = new InputStreamReader(in);
      CharArrayWriter writer = new CharArrayWriter();
      while ((c = reader.read(chBuff, 0, buffSize)) != -1) {
        writer.write(chBuff, 0, c);
      }
      return writer.toString();
    } catch (Exception e) {
      throw new RuntimeException(
          "Caught an exception while trying to load resource '" + name + "'", e);
    }
  }
}
