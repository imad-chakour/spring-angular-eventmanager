package com.example.analyticsservice.controller;

import com.example.analyticsservice.model.CampaignMetrics;
import com.example.analyticsservice.model.EventMetrics;
import com.example.analyticsservice.service.AnalyticsService;
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
    @GetMapping("/campaigns")
    public Iterable<CampaignMetrics> getCampaignMetrics() {
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
    @GetMapping("/events")
    public Iterable<EventMetrics> getEventMetrics() {
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

}