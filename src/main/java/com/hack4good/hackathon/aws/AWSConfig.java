package com.hack4good.hackathon.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.transcribestreaming.TranscribeStreamingAsyncClient;

@Configuration
public class AWSConfig {
    @Bean
    public TranscribeStreamingAsyncClient transcribeStreamingAsyncClient() {
        return TranscribeStreamingAsyncClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.EU_SOUTH_2) // Reemplaza con la región que prefieras
                .build();
    }
    @Bean
    public ComprehendClient comprehendClient() {
        return ComprehendClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.EU_SOUTH_2) // Reemplaza con la región que prefieras
                .build();
    }
}
