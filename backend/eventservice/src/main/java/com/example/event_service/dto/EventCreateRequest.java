package com.example.event_service.dto;

import com.example.event_service.model.EventFormat;
import com.example.event_service.model.EventStatus;
import com.example.event_service.model.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
public class EventCreateRequest {
    private String title;
    private String description;
    private EventType type;
    private EventFormat format;
    
    // Accepter les dates comme String pour plus de flexibilité
    @JsonProperty("startDate")
    private String startDate;
    
    @JsonProperty("endDate")
    private String endDate;
    
    private String location;
    private Integer maxCapacity;
    private EventStatus status;
    
    @JsonProperty("organizerId")
    private Long organizerId;
    
    // Méthodes pour convertir les strings en LocalDateTime
    public LocalDateTime getStartDateAsLocalDateTime() {
        if (startDate == null || startDate.isEmpty()) {
            return null;
        }
        try {
            if (startDate.contains("T")) {
                if (startDate.length() == 19) {
                    return LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                } else {
                    return LocalDateTime.parse(startDate);
                }
            } else {
                return LocalDateTime.parse(startDate + "T00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            }
        } catch (DateTimeParseException e) {
            System.err.println("❌ Erreur de parsing de startDate: " + startDate);
            throw new IllegalArgumentException("Format de date invalide pour startDate: " + startDate + ". Format attendu: yyyy-MM-ddTHH:mm:ss");
        }
    }
    
    public LocalDateTime getEndDateAsLocalDateTime() {
        if (endDate == null || endDate.isEmpty()) {
            return null;
        }
        try {
            if (endDate.contains("T")) {
                if (endDate.length() == 19) {
                    return LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                } else {
                    return LocalDateTime.parse(endDate);
                }
            } else {
                return LocalDateTime.parse(endDate + "T23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            }
        } catch (DateTimeParseException e) {
            System.err.println("❌ Erreur de parsing de endDate: " + endDate);
            throw new IllegalArgumentException("Format de date invalide pour endDate: " + endDate + ". Format attendu: yyyy-MM-ddTHH:mm:ss");
        }
    }
}
