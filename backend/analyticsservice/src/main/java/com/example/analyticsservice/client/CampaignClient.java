package com.example.analyticsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "campaignservice", path = "/api/campaigns")
public interface CampaignClient {

    @GetMapping("/{id}")
    Map<String, Object> getCampaignById(@PathVariable("id") Long id);
}


