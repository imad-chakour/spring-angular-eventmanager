package com.example.msproducts.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class MessageController {

    //@Value : injection simple et rapide d'une seule valeur
    @Value("${app.message}")
    private String message;

    @Value("${app.feature-enabled}")
    private boolean featureEnabled;

    @GetMapping("/message")
    public String getMessage() {
        return "Message : " + message + " | featureEnabled";
    }
}