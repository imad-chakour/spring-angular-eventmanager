package com.example.notificationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Client Feign pour communiquer avec le Campaign Service
 * Utilisé pour enrichir les notifications liées aux campagnes
 */
@FeignClient(name = "campaignservice", path = "/api/campaigns")
public interface CampaignClient {

    /**
     * Récupère une campagne par son ID
     * @param id ID de la campagne
     * @return Map contenant les informations de la campagne
     */
    @GetMapping("/{id}")
    Map<String, Object> getCampaignById(@PathVariable("id") Long id);
}
