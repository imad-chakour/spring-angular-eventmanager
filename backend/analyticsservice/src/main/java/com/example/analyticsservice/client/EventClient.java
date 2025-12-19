package com.example.analyticsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
 * Client Feign pour communiquer avec le Event Service
 * Utilisé pour valider l'existence d'un événement lors de la création de métriques
 */
@FeignClient(name = "eventservice", path = "/api/events")
public interface EventClient {

    /**
     * Récupère un événement par son ID
     * @param id ID de l'événement
     * @return Map contenant les informations de l'événement
     */
    @GetMapping("/{id}")
    Map<String, Object> getEventById(@PathVariable("id") Long id);

    /**
     * Récupère tous les événements
     * @return Liste des événements
     */
    @GetMapping
    List<Map<String, Object>> getAllEvents();

    /**
     * Récupère les inscriptions pour un événement
     * @param eventId ID de l'événement
     * @return Liste des inscriptions
     */
    @GetMapping("/registrations/event/{eventId}")
    List<Map<String, Object>> getRegistrationsByEvent(@PathVariable("eventId") Long eventId);
}


