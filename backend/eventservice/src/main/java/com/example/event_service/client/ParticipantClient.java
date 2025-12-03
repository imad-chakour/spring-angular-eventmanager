package com.example.event_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "participantservice", path = "/api/participants")
public interface ParticipantClient {

    @GetMapping("/{id}")
    Map<String, Object> getParticipantById(@PathVariable("id") Long id);
}


