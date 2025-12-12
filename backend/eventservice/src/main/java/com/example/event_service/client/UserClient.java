package com.example.event_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Client Feign pour communiquer avec le User Service
 * Utilisé pour valider l'existence de l'organisateur lors de la création/modification d'un événement
 */
@FeignClient(name = "userservice", path = "/api/users")
public interface UserClient {

    /**
     * Récupère un utilisateur par son ID
     * @param id ID de l'utilisateur
     * @return Map contenant les informations de l'utilisateur
     */
    @GetMapping("/{id}")
    Map<String, Object> getUserById(@PathVariable("id") Long id);
}


