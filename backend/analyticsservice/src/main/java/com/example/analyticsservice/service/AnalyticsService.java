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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

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

    /**
     * Calcule automatiquement les métriques pour toutes les campagnes
     * @return Liste des métriques calculées
     */
    public List<CampaignMetrics> calculateCampaignMetrics() {
        System.out.println("=== AnalyticsService: calculateCampaignMetrics ===");
        List<CampaignMetrics> calculatedMetrics = new ArrayList<>();
        
        try {
            List<Map<String, Object>> campaigns = campaignClient.getAllCampaigns();
            System.out.println("Found " + (campaigns != null ? campaigns.size() : 0) + " campaigns");
            
            if (campaigns != null) {
                for (Map<String, Object> campaign : campaigns) {
                    Long campaignId = getLongValue(campaign, "id");
                    if (campaignId == null) continue;
                    
                    // Vérifier si des métriques existent déjà
                    List<CampaignMetrics> existing = campaignMetricsRepository.findByCampaignId(campaignId);
                    CampaignMetrics metrics;
                    
                    if (existing != null && !existing.isEmpty()) {
                        // Mettre à jour les métriques existantes
                        metrics = existing.get(0);
                    } else {
                        // Créer de nouvelles métriques
                        metrics = new CampaignMetrics();
                        metrics.setCampaignId(campaignId);
                        metrics.setCampaignReference(getStringValue(campaign, "reference"));
                    }
                    
                    // Calculer les métriques (simulation - à adapter selon vos besoins)
                    // Pour l'instant, on génère des valeurs de test
                    metrics.setEmailsSent(metrics.getEmailsSent() != null ? metrics.getEmailsSent() : 0);
                    metrics.setEmailsDelivered(metrics.getEmailsDelivered() != null ? metrics.getEmailsDelivered() : 0);
                    metrics.setEmailsOpened(metrics.getEmailsOpened() != null ? metrics.getEmailsOpened() : 0);
                    metrics.setClicks(metrics.getClicks() != null ? metrics.getClicks() : 0);
                    metrics.setConversions(metrics.getConversions() != null ? metrics.getConversions() : 0);
                    
                    // Calculer les taux
                    if (metrics.getEmailsSent() != null && metrics.getEmailsSent() > 0) {
                        metrics.setOpenRate(metrics.getEmailsOpened() != null ? 
                            (double) metrics.getEmailsOpened() / metrics.getEmailsSent() * 100 : 0.0);
                        metrics.setClickRate(metrics.getClicks() != null ? 
                            (double) metrics.getClicks() / metrics.getEmailsSent() * 100 : 0.0);
                        metrics.setConversionRate(metrics.getConversions() != null ? 
                            (double) metrics.getConversions() / metrics.getEmailsSent() * 100 : 0.0);
                    }
                    
                    metrics.setCalculationDate(LocalDateTime.now());
                    CampaignMetrics saved = campaignMetricsRepository.save(metrics);
                    calculatedMetrics.add(saved);
                    System.out.println("Calculated metrics for campaign ID: " + campaignId);
                }
            }
        } catch (Exception e) {
            System.err.println("Error calculating campaign metrics: " + e.getMessage());
            e.printStackTrace();
        }
        
        return calculatedMetrics;
    }

    /**
     * Calcule automatiquement les métriques pour tous les événements
     * @return Liste des métriques calculées
     */
    public List<EventMetrics> calculateEventMetrics() {
        System.out.println("=== AnalyticsService: calculateEventMetrics ===");
        List<EventMetrics> calculatedMetrics = new ArrayList<>();
        
        try {
            List<Map<String, Object>> events = eventClient.getAllEvents();
            System.out.println("Found " + (events != null ? events.size() : 0) + " events");
            
            if (events != null) {
                for (Map<String, Object> event : events) {
                    Long eventId = getLongValue(event, "id");
                    if (eventId == null) continue;
                    
                    // Récupérer les inscriptions pour cet événement
                    List<Map<String, Object>> registrations = eventClient.getRegistrationsByEvent(eventId);
                    int totalRegistrations = registrations != null ? registrations.size() : 0;
                    
                    // Compter les inscriptions confirmées
                    long confirmedRegistrations = registrations != null ? 
                        registrations.stream()
                            .filter(reg -> "CONFIRMED".equals(getStringValue(reg, "status")))
                            .count() : 0;
                    
                    // Vérifier si des métriques existent déjà
                    List<EventMetrics> existing = eventMetricsRepository.findByEventId(eventId);
                    EventMetrics metrics;
                    
                    if (existing != null && !existing.isEmpty()) {
                        // Mettre à jour les métriques existantes
                        metrics = existing.get(0);
                    } else {
                        // Créer de nouvelles métriques
                        metrics = new EventMetrics();
                        metrics.setEventId(eventId);
                    }
                    
                    // Mettre à jour les métriques
                    metrics.setTotalRegistrations(totalRegistrations);
                    metrics.setConfirmedRegistrations((int) confirmedRegistrations);
                    
                    // Calculer les taux
                    Integer maxCapacity = getIntegerValue(event, "maxCapacity");
                    Integer currentParticipants = getIntegerValue(event, "currentParticipants");
                    
                    if (maxCapacity != null && maxCapacity > 0) {
                        metrics.setActualAttendance(currentParticipants != null ? currentParticipants : 0);
                        metrics.setAttendanceRate(currentParticipants != null ? 
                            (double) currentParticipants / maxCapacity * 100 : 0.0);
                    }
                    
                    if (totalRegistrations > 0) {
                        long cancelled = registrations != null ? 
                            registrations.stream()
                                .filter(reg -> "CANCELLED".equals(getStringValue(reg, "status")))
                                .count() : 0;
                        metrics.setCancellationRate((double) cancelled / totalRegistrations * 100);
                    }
                    
                    metrics.setCalculationDate(LocalDateTime.now());
                    EventMetrics saved = eventMetricsRepository.save(metrics);
                    calculatedMetrics.add(saved);
                    System.out.println("Calculated metrics for event ID: " + eventId + 
                        " - Total registrations: " + totalRegistrations);
                }
            }
        } catch (Exception e) {
            System.err.println("Error calculating event metrics: " + e.getMessage());
            e.printStackTrace();
        }
        
        return calculatedMetrics;
    }

    // Helper methods
    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }
}