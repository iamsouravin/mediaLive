
package com.amazonaws.examples;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.medialive.MediaLiveClient;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * The module containing all dependencies required by the {@link App}.
 */
public class DependencyFactory {

    private DependencyFactory() {}

    /**
     * @return an instance of S3Client
     */
    public static S3Client s3Client() {
        return S3Client.builder()
                       .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                       .region(Region.US_WEST_2)
                       .httpClientBuilder(UrlConnectionHttpClient.builder())
                       .build();
    }

    /**
     * @return an instance of MediaLiveClient
     */
    public static MediaLiveClient mediaLiveClient() {
        return MediaLiveClient.builder()
                        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                        .region(Region.US_WEST_2)
                        .httpClientBuilder(UrlConnectionHttpClient.builder())
                        .build();
    }

    /**
     * @return an instance of IamClient
     */
    public static IamClient iamClient() {
        return IamClient.builder()
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(Region.AWS_GLOBAL)
            .httpClientBuilder(UrlConnectionHttpClient.builder())
            .build();
    }
}
