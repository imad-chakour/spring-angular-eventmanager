package com.example.analyticsservice.service;

import com.example.analyticsservice.client.CampaignClient;
import com.example.analyticsservice.client.EventClient;
import com.example.analyticsservice.model.CampaignMetrics;
import com.example.analyticsservice.model.EventMetrics;
import com.example.analyticsservice.repository.CampaignMetricsRepository;
import com.example.analyticsservice.repository.EventMetricsRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Data
@Service
public class AnalyticsService {

    @Autowired
    private CampaignMetricsRepository campaignMetricsRepository;

    @Autowired
    private EventMetricsRepository eventMetricsRepository;

    @Autowired
    private CampaignClient campaignClient;

    @Autowired
    private EventClient eventClient;

    public Optional<CampaignMetrics> getCampaignMetrics(final Long id) {
        return campaignMetricsRepository.findById(id);
    }

    public Iterable<CampaignMetrics> getCampaignMetrics() {
        return campaignMetricsRepository.findAll();
    }

    public Iterable<CampaignMetrics> getCampaignMetricsByCampaign(Long campaignId) {
        return campaignMetricsRepository.findByCampaignId(campaignId);
    }

    public CampaignMetrics saveCampaignMetrics(CampaignMetrics metrics) {
        // Validate campaign exists through Campaign Service
        if (metrics.getCampaignId() != null) {
            Map<String, Object> campaign = campaignClient.getCampaignById(metrics.getCampaignId());
            if (campaign == null || campaign.isEmpty()) {
                throw new RuntimeException("Campaign not found with id " + metrics.getCampaignId());
            }
        }
        metrics.setCalculationDate(LocalDateTime.now());
        return campaignMetricsRepository.save(metrics);
    }

    public Optional<EventMetrics> getEventMetrics(final Long id) {
        return eventMetricsRepository.findById(id);
    }

    public Iterable<EventMetrics> getEventMetrics() {
        return eventMetricsRepository.findAll();
    }

    public Iterable<EventMetrics> getEventMetricsByEvent(Long eventId) {
        return eventMetricsRepository.findByEventId(eventId);
    }

    public EventMetrics saveEventMetrics(EventMetrics metrics) {
        // Validate event exists through Event Service
        if (metrics.getEventId() != null) {
            Map<String, Object> event = eventClient.getEventById(metrics.getEventId());
            if (event == null || event.isEmpty()) {
                throw new RuntimeException("Event not found with id " + metrics.getEventId());
            }
        }
        metrics.setCalculationDate(LocalDateTime.now());
        return eventMetricsRepository.save(metrics);
    }
}