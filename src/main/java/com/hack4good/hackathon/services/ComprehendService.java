package com.hack4good.hackathon.services;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentRequest;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentResponse;

@Service
public class ComprehendService {
    private final ComprehendClient comprehendClient;
    public ComprehendService(ComprehendClient comprehendClient) {
        this.comprehendClient = comprehendClient;
    }

    public String detectSentiment(String text) {
        DetectSentimentRequest request = DetectSentimentRequest.builder()
                .text(text)
                .languageCode("es")
                .build();

        DetectSentimentResponse response = comprehendClient.detectSentiment(request);
        return response.sentiment().toString();
    }
}
