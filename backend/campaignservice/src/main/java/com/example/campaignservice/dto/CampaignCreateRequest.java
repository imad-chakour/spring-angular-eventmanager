package com.example.campaignservice.dto;

import com.example.campaignservice.model.CampaignStatus;
import com.example.campaignservice.model.Channel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Data
public class CampaignCreateRequest {
    private String name;
    private String description;
    
    // Accepter les dates comme String pour plus de flexibilité
    @JsonProperty("startDate")
    private String startDate;
    
    @JsonProperty("endDate")
    private String endDate;
    
    private Double budget;
    private CampaignStatus status;
    private Channel channel;
    
    @JsonProperty("organizerId")
    private Long organizerId;
    
    @JsonProperty("targetSegments")
    private List<String> targetSegments = new ArrayList<>();
    
    // Méthodes pour convertir les strings en LocalDateTime
    public LocalDateTime getStartDateAsLocalDateTime() {
        if (startDate == null || startDate.isEmpty()) {
            return null;
        }
        try {
            // Essayer plusieurs formats
            if (startDate.contains("T")) {
                // Format ISO: yyyy-MM-ddTHH:mm:ss ou yyyy-MM-ddTHH:mm:ss.SSS
                if (startDate.length() == 19) {
                    return LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                } else {
                    return LocalDateTime.parse(startDate);
                }
            } else {
                // Format date seule: yyyy-MM-dd
                return LocalDateTime.parse(startDate + "T00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            }
        } catch (DateTimeParseException e) {
            System.err.println("❌ Erreur de parsing de startDate: " + startDate);
            System.err.println("   Message: " + e.getMessage());
            throw new IllegalArgumentException("Format de date invalide pour startDate: " + startDate + ". Format attendu: yyyy-MM-ddTHH:mm:ss");
        }
    }
    
    public LocalDateTime getEndDateAsLocalDateTime() {
        if (endDate == null || endDate.isEmpty()) {
            return null;
        }
        try {
            // Essayer plusieurs formats
            if (endDate.contains("T")) {
                // Format ISO: yyyy-MM-ddTHH:mm:ss ou yyyy-MM-ddTHH:mm:ss.SSS
                if (endDate.length() == 19) {
                    return LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                } else {
                    return LocalDateTime.parse(endDate);
                }
            } else {
                // Format date seule: yyyy-MM-dd
                return LocalDateTime.parse(endDate + "T23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            }
        } catch (DateTimeParseException e) {
            System.err.println("❌ Erreur de parsing de endDate: " + endDate);
            System.err.println("   Message: " + e.getMessage());
            throw new IllegalArgumentException("Format de date invalide pour endDate: " + endDate + ". Format attendu: yyyy-MM-ddTHH:mm:ss");
        }
    }
}
