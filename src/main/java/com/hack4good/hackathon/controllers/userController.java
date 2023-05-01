package com.hack4good.hackathon.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class userController {
    @GetMapping("/")
    public  String demo() {
        return "Hola Anya y quim";
    }
}
