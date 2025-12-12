package com.example.reactivegateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
public class GatewayTestController {

    @GetMapping("/gateway/status")
    public Mono<Map<String, String>> getStatus() {
        return Mono.just(Map.of(
                "status", "UP",
                "service", "Reactive Gateway",
                "port", "1111",
                "message", "Gateway is running"
        ));
    }

    @GetMapping("/health")
    public Mono<Map<String, String>> health() {
        return Mono.just(Map.of("status", "healthy"));
    }

    @GetMapping("/gateway/routes")
    public Mono<Map<String, String>> getRoutes() {
        return Mono.just(Map.of(
                "/api/users/**", "→ USERSERVICE (utilisé aussi pour /participants via filtrage)",
                "/api/campaigns/**", "→ CAMPAIGNSERVICE",
                "/api/events/**", "→ EVENTSERVICE",
                "/api/analytics/**", "→ ANALYTICSSERVICE",
                "/api/notifications/**", "→ NOTIFICATIONSERVICE",
                "/gateway/**", "→ Gateway test endpoints"
        ));
    }
}