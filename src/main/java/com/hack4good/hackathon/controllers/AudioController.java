package com.hack4good.hackathon.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.StartTranscriptionJobRequest;
import software.amazon.awssdk.services.transcribe.model.Media;
import software.amazon.awssdk.services.transcribe.model.StartTranscriptionJobResponse;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
public class AudioController {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private TranscribeClient transcribeClient;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @PostMapping("/upload")
    public String uploadAudio(@RequestParam("file") MultipartFile file) {
        // Almacenar el archivo en S3
        String fileName = file.getOriginalFilename();
        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .build();
            File tempFile = convertMultiPartToFile(file);
            s3Client.putObject(objectRequest, RequestBody.fromFile(tempFile));

            tempFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al cargar el archivo: " + e.getMessage();
        }

        // Continuar con la transcripción del audio en AWS Transcribe
        String result = startTranscriptionJob(fileName);
        //return "Archivo recibido y almacenado en S3";
        return result;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("temp", file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        return tempFile;
    }

    private String startTranscriptionJob(String fileName) {


        String jobName = "transcribeJob-" + UUID.randomUUID();
        String fileUrl = "s3://" + bucketName + "/" + fileName;
        Media media = Media.builder().mediaFileUri(fileUrl).build();


        StartTranscriptionJobRequest request = StartTranscriptionJobRequest.builder().languageCode("es-ES").mediaFormat("mp3")
                .mediaSampleRateHertz(16000).transcriptionJobName(jobName).media(media).build();

        StartTranscriptionJobResponse result = transcribeClient.startTranscriptionJob(request);


        return result.transcriptionJob().transcript().toString();
    }
}