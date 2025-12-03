package com.example.analyticsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "event-service", path = "/api/events")
public interface EventClient {

    @GetMapping("/{id}")
    Map<String, Object> getEventById(@PathVariable("id") Long id);
}


