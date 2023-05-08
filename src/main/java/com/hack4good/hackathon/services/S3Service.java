package com.hack4good.hackathon.services;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.InputStream;


@Service
public class S3Service {
    private final S3Client s3Client;
    private static final String BUCKET_NAME = "wav-transcribe-bucket";
    public S3Service(S3Client s3Client){
        this.s3Client = s3Client;
    }
    public void uploadFile(String key, File file) {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(key)
                        .build(),
                RequestBody.fromFile(file));
    }
    public InputStream downloadFile(String key) {
        return s3Client.getObject(GetObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(key)
                        .build(),
                ResponseTransformer.toInputStream());
    }
}
