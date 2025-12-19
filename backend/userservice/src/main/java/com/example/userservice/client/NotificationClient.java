package com.example.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Client Feign pour communiquer avec le Notification Service
 * Utilisé pour créer des notifications lors de la création de compte
 */
@FeignClient(name = "notificationservice", path = "/api/notifications")
public interface NotificationClient {

    /**
     * Crée une notification
     * @param notification Map contenant les informations de la notification
     * @return Map contenant la notification créée
     */
    @PostMapping("/from-map")
    Map<String, Object> createNotification(@RequestBody Map<String, Object> notification);
}

