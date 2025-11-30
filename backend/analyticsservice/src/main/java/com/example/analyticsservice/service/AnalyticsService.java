package com.example.analyticsservice.service;

import com.example.analyticsservice.model.CampaignMetrics;
import com.example.analyticsservice.model.EventMetrics;
import com.example.analyticsservice.repository.CampaignMetricsRepository;
import com.example.analyticsservice.repository.EventMetricsRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Service
public class AnalyticsService {

    @Autowired
    private CampaignMetricsRepository campaignMetricsRepository;

    @Autowired
    private EventMetricsRepository eventMetricsRepository;

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
        metrics.setCalculationDate(LocalDateTime.now());
        return eventMetricsRepository.save(metrics);
    }
}