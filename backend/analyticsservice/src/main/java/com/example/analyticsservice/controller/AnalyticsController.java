package com.example.analyticsservice.controller;

import com.example.analyticsservice.model.CampaignMetrics;
import com.example.analyticsservice.model.EventMetrics;
import com.example.analyticsservice.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    @GetMapping("/campaigns")
    public List<CampaignMetrics> getCampaignMetrics() {
        Iterable<CampaignMetrics> metrics = analyticsService.getCampaignMetrics();
        return StreamSupport.stream(metrics.spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/campaigns/{campaignId}")
    public List<CampaignMetrics> getCampaignMetricsByCampaign(@PathVariable("campaignId") final Long campaignId) {
        Iterable<CampaignMetrics> metrics = analyticsService.getCampaignMetricsByCampaign(campaignId);
        return StreamSupport.stream(metrics.spliterator(), false)
                .collect(Collectors.toList());
    }

    @PostMapping("/campaigns")
    public CampaignMetrics createCampaignMetrics(@RequestBody CampaignMetrics metrics) {
        return analyticsService.saveCampaignMetrics(metrics);
    }

    // Event Metrics endpoints
    @GetMapping("/events")
    public List<EventMetrics> getEventMetrics() {
        Iterable<EventMetrics> metrics = analyticsService.getEventMetrics();
        return StreamSupport.stream(metrics.spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/{eventId}")
    public List<EventMetrics> getEventMetricsByEvent(@PathVariable("eventId") final Long eventId) {
        Iterable<EventMetrics> metrics = analyticsService.getEventMetricsByEvent(eventId);
        return StreamSupport.stream(metrics.spliterator(), false)
                .collect(Collectors.toList());
    }

    @PostMapping("/events")
    public EventMetrics createEventMetrics(@RequestBody EventMetrics metrics) {
        return analyticsService.saveEventMetrics(metrics);
    }

    // Endpoints pour calculer automatiquement les métriques
    @PostMapping("/campaigns/calculate")
    public List<CampaignMetrics> calculateCampaignMetrics() {
        System.out.println("=== AnalyticsController: calculateCampaignMetrics ===");
        return analyticsService.calculateCampaignMetrics();
    }

    @PostMapping("/events/calculate")
    public List<EventMetrics> calculateEventMetrics() {
        System.out.println("=== AnalyticsController: calculateEventMetrics ===");
        return analyticsService.calculateEventMetrics();
    }

    @PostMapping("/calculate-all")
    public Map<String, Object> calculateAllMetrics() {
        System.out.println("=== AnalyticsController: calculateAllMetrics ===");
        System.out.println("Starting calculation of all metrics...");
        
        try {
            List<CampaignMetrics> campaignMetrics = analyticsService.calculateCampaignMetrics();
            System.out.println("Campaign metrics calculated: " + (campaignMetrics != null ? campaignMetrics.size() : 0));
            
            List<EventMetrics> eventMetrics = analyticsService.calculateEventMetrics();
            System.out.println("Event metrics calculated: " + (eventMetrics != null ? eventMetrics.size() : 0));
            
            Map<String, Object> result = Map.of(
                "campaignMetrics", campaignMetrics != null ? campaignMetrics : List.of(),
                "eventMetrics", eventMetrics != null ? eventMetrics : List.of(),
                "message", "Metrics calculated successfully"
            );
            
            System.out.println("Returning result with " + campaignMetrics.size() + " campaign metrics and " + eventMetrics.size() + " event metrics");
            return result;
        } catch (Exception e) {
            System.err.println("❌ ERROR in calculateAllMetrics: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

}