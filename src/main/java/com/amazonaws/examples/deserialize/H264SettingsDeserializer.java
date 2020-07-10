package com.amazonaws.examples.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.medialive.model.H264AdaptiveQuantization;
import software.amazon.awssdk.services.medialive.model.H264ColorMetadata;
import software.amazon.awssdk.services.medialive.model.H264EntropyEncoding;
import software.amazon.awssdk.services.medialive.model.H264FlickerAq;
import software.amazon.awssdk.services.medialive.model.H264FramerateControl;
import software.amazon.awssdk.services.medialive.model.H264GopBReference;
import software.amazon.awssdk.services.medialive.model.H264GopSizeUnits;
import software.amazon.awssdk.services.medialive.model.H264Level;
import software.amazon.awssdk.services.medialive.model.H264LookAheadRateControl;
import software.amazon.awssdk.services.medialive.model.H264ParControl;
import software.amazon.awssdk.services.medialive.model.H264Profile;
import software.amazon.awssdk.services.medialive.model.H264RateControlMode;
import software.amazon.awssdk.services.medialive.model.H264SceneChangeDetect;
import software.amazon.awssdk.services.medialive.model.H264Settings;
import software.amazon.awssdk.services.medialive.model.H264SpatialAq;
import software.amazon.awssdk.services.medialive.model.H264Syntax;
import software.amazon.awssdk.services.medialive.model.H264TemporalAq;

import static com.amazonaws.examples.deserialize.JsonUtils.validateTokenType;

public class H264SettingsDeserializer extends JsonDeserializer<H264Settings> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public H264SettingsDeserializer() {
    logger.info("Loading...");
  }

  @Override public H264Settings deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {

    JsonToken token = parser.nextToken();
    validateTokenType(parser, token, JsonToken.START_OBJECT);

    token = parser.nextToken();
    H264Settings.Builder builder = H264Settings.builder();

    while (token != JsonToken.END_OBJECT) {
      checkAndConsumeColorMetadata(parser, token, builder);
      checkAndConsumeAdaptiveQuantization(parser, token, builder);
      checkAndConsumeBitrate(parser, token, builder);
      checkAndConsumeEntropyEncoding(parser, token, builder);
      checkAndConsumeFlickerAq(parser, token, builder);
      checkAndConsumeFramerateControl(parser, token, builder);
      checkAndConsumeFramerateNumerator(parser, token, builder);
      checkAndConsumeFramerateDenominator(parser, token, builder);
      checkAndConsumeGopBReference(parser, token, builder);
      checkAndConsumeGopNumBFrames(parser, token, builder);
      checkAndConsumeGopSize(parser, token, builder);
      checkAndConsumeGopSizeUnits(parser, token, builder);
      checkAndConsumeLevel(parser, token, builder);
      checkAndConsumeLookAheadRateControl(parser, token, builder);
      checkAndConsumeParControl(parser, token, builder);
      checkAndConsumeProfile(parser, token, builder);
      checkAndConsumeRateControlMode(parser, token, builder);
      checkAndConsumeSyntax(parser, token, builder);
      checkAndConsumeSceneChangeDetect(parser, token, builder);
      checkAndConsumeSpatialAq(parser, token, builder);
      checkAndConsumeTemporalAq(parser, token, builder);

      token = parser.nextToken();
    }

    validateTokenType(parser, token, JsonToken.END_OBJECT);

    return builder.build();
  }

  private void checkAndConsumeTemporalAq(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "temporalAq".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.temporalAq(H264TemporalAq.fromValue(parser.getValueAsString("ENABLED")));
    }
  }

  private void checkAndConsumeSpatialAq(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "spatialAq".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.spatialAq(H264SpatialAq.fromValue(parser.getValueAsString("ENABLED")));
    }
  }

  private void checkAndConsumeSceneChangeDetect(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "sceneChangeDetect".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.sceneChangeDetect(
          H264SceneChangeDetect.fromValue(parser.getValueAsString("ENABLED")));
    }
  }

  private void checkAndConsumeSyntax(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "syntax".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.syntax(H264Syntax.fromValue(parser.getValueAsString("DEFAULT")));
    }
  }

  private void checkAndConsumeRateControlMode(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "rateControlMode".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.rateControlMode(H264RateControlMode.fromValue(parser.getValueAsString()));
    }
  }

  private void checkAndConsumeProfile(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "profile".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.profile(H264Profile.fromValue(parser.getValueAsString()));
    }
  }

  private void checkAndConsumeParControl(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "parControl".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.parControl(H264ParControl.fromValue(parser.getValueAsString("SPECIFIED")));
    }
  }

  private void checkAndConsumeLookAheadRateControl(JsonParser parser,
      JsonToken token, H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "lookAheadRateControl".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.lookAheadRateControl(
          H264LookAheadRateControl.fromValue(parser.getValueAsString("HIGH")));
    }
  }

  private void checkAndConsumeLevel(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "level".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.level(H264Level.fromValue(parser.getValueAsString()));
    }
  }

  private void checkAndConsumeGopSizeUnits(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "gopSizeUnits".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.gopSizeUnits(H264GopSizeUnits.fromValue(parser.getValueAsString("FRAMES")));
    }
  }

  private void checkAndConsumeGopSize(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "gopSize".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_NUMBER_INT);

      builder.gopSize(parser.getValueAsDouble());
    }
  }

  private void checkAndConsumeGopNumBFrames(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "gopNumBFrames".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_NUMBER_INT);

      builder.gopNumBFrames(parser.getValueAsInt());
    }
  }

  private void checkAndConsumeGopBReference(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "gopBReference".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.gopBReference(H264GopBReference.fromValue(parser.getValueAsString()));
    }
  }

  private void checkAndConsumeFramerateDenominator(JsonParser parser,
      JsonToken token, H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "framerateDenominator".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_NUMBER_INT);

      builder.framerateDenominator(parser.getValueAsInt());
    }
  }

  private void checkAndConsumeFramerateNumerator(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "framerateNumerator".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_NUMBER_INT);

      builder.framerateNumerator(parser.getValueAsInt());
    }
  }

  private void checkAndConsumeFramerateControl(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "framerateControl".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.framerateControl(
          H264FramerateControl.fromValue(parser.getValueAsString("SPECIFIED")));
    }
  }

  private void checkAndConsumeFlickerAq(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "flickerAq".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.flickerAq(H264FlickerAq.fromValue(parser.getValueAsString("ENABLED")));
    }
  }

  private void checkAndConsumeEntropyEncoding(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "entropyEncoding".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.entropyEncoding(H264EntropyEncoding.fromValue(parser.getValueAsString()));
    }
  }

  private void checkAndConsumeBitrate(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "bitrate".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_NUMBER_INT);

      builder.bitrate(parser.getValueAsInt());
    }
  }

  private void checkAndConsumeAdaptiveQuantization(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "adaptiveQuantization".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.adaptiveQuantization(
          H264AdaptiveQuantization.fromValue(parser.getValueAsString("HIGH")));
    }
  }

  private void checkAndConsumeColorMetadata(JsonParser parser, JsonToken token,
      H264Settings.Builder builder) throws IOException {

    if (token == JsonToken.FIELD_NAME && "colorMetadata".equals(parser.getText())) {
      token = parser.nextToken();
      validateTokenType(parser, token, JsonToken.VALUE_STRING);

      builder.colorMetadata(H264ColorMetadata.fromValue(parser.getValueAsString("INSERT")));
    }
  }
}
