package com.hack4good.hackathon.controllers;

import com.hack4good.hackathon.services.ComprehendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.comprehend.model.Entity;

import java.io.IOException;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    ComprehendService comprehendService;
    @GetMapping("/comprehend")
    public  String demo() {
        return comprehendService.detectSentiment("Buenos dias estamos muy bien, esta prueba funciona muy bien");
    }

    @GetMapping("/hola")
    public  String hola() {
        return "hola";
    }

    @GetMapping("/comprehend/test")
    public List<Entity> comprehendTest() throws IOException {
        ClassPathResource resource = new ClassPathResource("data/sample_text.json");
        String myString = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
        return comprehendService.detectEntities(myString);
    }
}
