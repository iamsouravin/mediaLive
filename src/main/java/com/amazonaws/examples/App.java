package com.amazonaws.examples;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.medialive.model.Channel;

/**
 * Lambda function entry point. You can change to use other pojo type or implement a different
 * RequestHandler.
 *
 * @see <a href=https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html>Lambda Java
 * Handler</a> for more information
 */
public class App implements RequestHandler<InputStream, Map<String, String>> {
  private final Logger logger = LoggerFactory.getLogger(App.class);

  private final ElementalMediaLiveProcessor emlProcessor;

  public App() {
    // Initialize the SDK client outside of the handler method so that it can be reused for subsequent invocations.
    // It is initialized when the class is loaded.
    emlProcessor = new ElementalMediaLiveProcessor();
    // Consider invoking a simple api here to pre-warm up the application, eg: dynamodb#listTables
  }

  @Override
  public Map<String, String> handleRequest(final InputStream input, final Context context) {
    logger.info("Received input...");
    try {
      Channel channel = emlProcessor.createChannel(input);
      Map<String, String> response = new HashMap<>();
      response.put("id", channel.id());
      response.put("arn", channel.arn());
      response.put("name", channel.name());
      response.put("state", channel.stateAsString());
      return response;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
