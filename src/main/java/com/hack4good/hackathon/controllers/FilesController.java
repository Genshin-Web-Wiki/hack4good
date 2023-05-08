package com.hack4good.hackathon.controllers;

import com.hack4good.hackathon.services.S3Service;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FilesController {

    private final S3Service s3Service;

    @Autowired
    public FilesController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf('.')) : "";
            String fileName = UUID.randomUUID().toString() + extension;

            byte[] fileBytes = file.getBytes();
            String base64File = Base64.getEncoder().encodeToString(fileBytes);
            byte[] decodedBytes = Base64.getDecoder().decode(base64File);
            InputStream inputStream = new ByteArrayInputStream(decodedBytes);
            Path tempFile = Files.createTempFile(fileName, null);
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

            s3Service.uploadFile(fileName, tempFile.toFile());
            Files.deleteIfExists(tempFile);
            return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully with name: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }

    @GetMapping("/download/{key}")
    public void downloadFile(@PathVariable String key, HttpServletResponse response) {
        try (InputStream inputStream = s3Service.downloadFile(key)) {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=" + key);
            inputStream.transferTo(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}