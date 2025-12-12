package com.example.notificationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Client Feign pour communiquer avec le User Service
 * Utilisé pour enrichir les notifications avec les informations utilisateur
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
