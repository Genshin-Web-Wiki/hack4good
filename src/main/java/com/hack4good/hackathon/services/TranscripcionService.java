package com.hack4good.hackathon.services;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.regions.*;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;

import software.amazon.awssdk.services.transcribe.TranscribeAsyncClient;
import software.amazon.awssdk.services.transcribe.model.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;
import software.amazon.awssdk.transfer.s3.model.FileDownload;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

public class TranscripcionService {

    private final String awsTranscribeBucket;
    private final String awsTranscribeJobName;
    private final Region awsRegion;
    private final AwsCredentialsProvider awsCredenciales;

    public TranscripcionService() {
        awsTranscribeBucket = System.getenv("AWS_TRANSCRIBE_BUCKET");
        awsTranscribeJobName = System.getenv("AWS_TRANSCRIBE_JOB_NAME");
        awsRegion = Region.of(System.getenv("AWS_REGION"));
        awsCredenciales = DefaultCredentialsProvider.create();
    }

    public String transcribirAudio(byte[] bytesAudio) {
        try {
// Cargar el archivo de audio a Amazon S3
            String nombreArchivo = "audio.mp3";
            String audioUrl = cargarAudioAS3(nombreArchivo, bytesAudio);


            // Crear un trabajo de transcripción en AWS Transcribe
            TranscribeAsyncClient transcribeClient = TranscribeAsyncClient.builder()
                    .region(awsRegion)
                    .credentialsProvider(awsCredenciales)
                    .build();

            StartTranscriptionJobRequest solicitudTranscripcion = StartTranscriptionJobRequest.builder()
                    .transcriptionJobName(awsTranscribeJobName)
                    .languageCode(LanguageCode.ES_US.toString())
                    .media(Media.builder().mediaFileUri(audioUrl).build())
                    .outputBucketName(awsTranscribeBucket)
                    .build();

            transcribeClient.startTranscriptionJob(solicitudTranscripcion);

            // Esperar a que el trabajo de transcripción termine
            GetTranscriptionJobRequest solicitudEstado = GetTranscriptionJobRequest.builder()
                    .transcriptionJobName(awsTranscribeJobName)
                    .build();

            String estado = "QUEUED";
            while (!estado.equals("COMPLETED")) {
                GetTranscriptionJobResponse resultadoEstado = transcribeClient.getTranscriptionJob(solicitudEstado).join();
                estado = resultadoEstado.transcriptionJob().transcriptionJobStatusAsString();
                Thread.sleep(5000); // Esperar 5 segundos antes de volver a revisar el estado
            }

            // Descargar y retornar el resultado de la transcripción
            String resultadoUrl = transcribeClient.getTranscriptionJob(solicitudEstado).join()
                    .transcriptionJob().transcript().transcriptFileUri();
            byte[] resultadoBytes = descargarResultadoDeTranscripcion(resultadoUrl);
            return new String(resultadoBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error al transcribir el audio", e);
        }
    }

    private String cargarAudioAS3(String nombreArchivo, byte[] bytesAudio) {
        S3Client s3Client = S3Client.builder()
                .region(awsRegion)
                .credentialsProvider(awsCredenciales)
                .build();
        InputStream inputStream = new ByteArrayInputStream(bytesAudio);
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(awsTranscribeBucket)
                .key(nombreArchivo)
                .build();
        s3Client.putObject(putRequest, AsyncRequestBody.fromBytes(bytesAudio)).join();
        return "s3://" + awsTranscribeBucket + "/" + nombreArchivo;
    }

    private byte[] descargarResultadoDeTranscripcion(String resultadoUrl) throws IOException {
        S3TransferManager transferManager = S3TransferManager.create();

        DownloadFileRequest downloadFileRequest =
                DownloadFileRequest.builder()
                        .getObjectRequest(req -> req.bucket("bucket").key("key"))
                        .destination(Paths.get("myFile.txt"))
                        .addTransferListener(LoggingTransferListener.create())
                        .build();

        FileDownload download = transferManager.downloadFile(downloadFileRequest);

        // Wait for the transfer to complete
        download.completionFuture().join();

        return responseBytes.asByteArray();
    }
}