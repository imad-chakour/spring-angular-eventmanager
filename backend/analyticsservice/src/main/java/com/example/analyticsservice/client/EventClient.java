package com.example.analyticsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Client Feign pour communiquer avec le Event Service
 * Utilisé pour valider l'existence d'un événement lors de la création de métriques
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


