package com.hack4good.hackathon.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
@Configuration
public class AWSConfig {
    @Bean
    public TranscribeClient transcribeClient() {
        return TranscribeClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.EU_WEST_2) // Reemplaza con la región que prefieras
                .build();
    }
    @Bean
    public ComprehendClient comprehendClient() {
        return ComprehendClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.EU_WEST_2) // Reemplaza con la región que prefieras
                .build();
    }
    @Bean
    public S3Client s3Client(){
        return S3Client.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.EU_WEST_2)
                .build();
    }
}
