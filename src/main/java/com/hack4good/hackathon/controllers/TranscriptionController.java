package com.hack4good.hackathon.controllers;

import com.hack4good.hackathon.services.TranscribeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transcribe")
public class TranscriptionController {

    private final TranscribeService transcribeService;

    public TranscriptionController(TranscribeService transcribeService) {
        this.transcribeService = transcribeService;
    }

    @PostMapping
    public ResponseEntity<Void> transcribeAndStore(@RequestParam("sourceKey") String sourceKey) {
        transcribeService.transcribeAndStore(sourceKey);
        return ResponseEntity.accepted().build();
    }
}