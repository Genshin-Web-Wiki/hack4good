package com.hack4good.hackathon.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AudioService {

    @Autowired
    private TranscripcionService transcripcionService;

    public String transcribirAudioMP3(byte[] bytesAudio) {
        return transcripcionService.transcribirAudio(bytesAudio);
    }
}