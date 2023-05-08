package com.hack4good.hackathon.services;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.*;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class TranscribeService {
    private final TranscribeClient transcribeClient;
    private final S3Client s3Client;

    public TranscribeService(TranscribeClient transcribeClient, S3Client s3Client) {
        this.transcribeClient = transcribeClient;
        this.s3Client = s3Client;
    }

    public void transcribeAndStore(String sourceKey) {
        String destinationBucket = "transcribe-job-wav";
        URI sourceUri;
        try {
            sourceUri = s3Client.utilities().getUrl(b -> b.bucket("wav-transcribe-bucket").key(sourceKey)).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error al convertir la URL del archivo en URI", e);
        }

        String transcribeJobName = "transcribe-job-" + System.currentTimeMillis();
        Settings settings = Settings.builder()
                .showSpeakerLabels(true)
                .maxSpeakerLabels(2) // Número máximo de speakers en el archivo de audio
                .build();
        StartTranscriptionJobRequest startTranscriptionJobRequest = StartTranscriptionJobRequest.builder()
                .languageCode(LanguageCode.ES_US)
                .transcriptionJobName(transcribeJobName)
                .mediaFormat(MediaFormat.WAV)
                .media(Media.builder().mediaFileUri(sourceUri.toString()).build())
                .outputBucketName(destinationBucket)
                .settings(settings)
                .build();

        transcribeClient.startTranscriptionJob(startTranscriptionJobRequest);
    }
}