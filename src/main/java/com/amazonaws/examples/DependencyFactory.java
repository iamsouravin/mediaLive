package com.amazonaws.examples;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.medialive.MediaLiveClient;

/**
 * The module containing all dependencies required by the {@link App}.
 */
public class DependencyFactory {

  private DependencyFactory() {
  }

  /**
   * @return an instance of MediaLiveClient
   */
  public static MediaLiveClient mediaLiveClient() {
    return MediaLiveClient.builder()
        .credentialsProvider(DefaultCredentialsProvider.create())
        .region(Region.US_WEST_2)
        .httpClientBuilder(UrlConnectionHttpClient.builder())
        .build();
  }

  /**
   * @return an instance of IamClient
   */
  public static IamClient iamClient() {
    return IamClient.builder()
        .credentialsProvider(DefaultCredentialsProvider.create())
        .region(Region.AWS_GLOBAL)
        .httpClientBuilder(UrlConnectionHttpClient.builder())
        .build();
  }
}
