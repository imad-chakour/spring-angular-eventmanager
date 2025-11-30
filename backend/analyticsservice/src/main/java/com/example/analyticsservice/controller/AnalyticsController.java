package com.example.analyticsservice.controller;

import com.example.analyticsservice.model.CampaignMetrics;
import com.example.analyticsservice.model.EventMetrics;
import com.example.analyticsservice.service.AnalyticsService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/")
    public String home() {
        return "Analytics Service is running!";
    }

    // Campaign Metrics endpoints
    @Retry(name = "campaignMetricsRetry", fallbackMethod = "fallbackCampaignMetricsCB")
    @CircuitBreaker(name = "campaignMetricsCB", fallbackMethod = "fallbackCampaignMetricsCB")
    @GetMapping("/campaigns")
    public Iterable<CampaignMetrics> getCampaignMetrics() {
        simulateRandomFailure();
        return analyticsService.getCampaignMetrics();
    }

    @GetMapping("/campaigns/{campaignId}")
    public Iterable<CampaignMetrics> getCampaignMetricsByCampaign(@PathVariable("campaignId") final Long campaignId) {
        return analyticsService.getCampaignMetricsByCampaign(campaignId);
    }

    @PostMapping("/campaigns")
    public CampaignMetrics createCampaignMetrics(@RequestBody CampaignMetrics metrics) {
        return analyticsService.saveCampaignMetrics(metrics);
    }

    // Event Metrics endpoints
    @Retry(name = "eventMetricsRetry", fallbackMethod = "fallbackEventMetricsCB")
    @CircuitBreaker(name = "eventMetricsCB", fallbackMethod = "fallbackEventMetricsCB")
    @GetMapping("/events")
    public Iterable<EventMetrics> getEventMetrics() {
        simulateRandomFailure();
        return analyticsService.getEventMetrics();
    }

    @GetMapping("/events/{eventId}")
    public Iterable<EventMetrics> getEventMetricsByEvent(@PathVariable("eventId") final Long eventId) {
        return analyticsService.getEventMetricsByEvent(eventId);
    }

    @PostMapping("/events")
    public EventMetrics createEventMetrics(@RequestBody EventMetrics metrics) {
        return analyticsService.saveEventMetrics(metrics);
    }

    private void simulateRandomFailure() {
        if (Math.random() < 0.3) {
            throw new RuntimeException("Simulated random failure in Analytics Service");
        }
    }

    public Iterable<CampaignMetrics> fallbackCampaignMetricsCB(Exception e) {
        System.err.println("Analytics Service Fallback (Campaign): " + e.getMessage());
        return List.of(
                new CampaignMetrics(1L, 1L, "CAMP-FALLBACK", 100, 90, 80, 50, 10,
                        5.0, 80.0, 55.6, 11.1, LocalDateTime.now())
        );
    }

    public Iterable<EventMetrics> fallbackEventMetricsCB(Exception e) {
        System.err.println("Analytics Service Fallback (Event): " + e.getMessage());
        return List.of(
                new EventMetrics(1L, 1L, 100, 80, 70, 70.0, 20.0, 4.5, LocalDateTime.now())
        );
    }
}