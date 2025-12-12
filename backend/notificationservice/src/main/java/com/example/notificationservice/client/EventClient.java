package com.example.notificationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Client Feign pour communiquer avec le Event Service
 * Utilisé pour enrichir les notifications liées aux événements
 */
@FeignClient(name = "event-service", path = "/api/events")
public interface EventClient {

    /**
     * Récupère un événement par son ID
     * @param id ID de l'événement
     * @return Map contenant les informations de l'événement
     */
    @GetMapping("/{id}")
    Map<String, Object> getEventById(@PathVariable("id") Long id);
}
