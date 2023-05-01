package com.hack4good.hackathon.controllers;

import com.hack4good.hackathon.services.ComprehendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class userController {
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

}
