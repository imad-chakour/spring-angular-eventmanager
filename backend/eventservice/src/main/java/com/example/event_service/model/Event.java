package com.example.event_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_seq")
    @SequenceGenerator(name = "event_seq", sequenceName = "EVENTS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "event_id", unique = true, nullable = false)
    private String eventId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private EventType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "format")
    private EventFormat format;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "location")
    private String location;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "current_participants")
    private Integer currentParticipants = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EventStatus status;

    // Reference to User service
    @Column(name = "organizer_id", nullable = false)
    private Long organizerId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Event() {
        this.createdAt = LocalDateTime.now();
        this.status = EventStatus.PLANIFIED;
    }

    public Event(long l, String s, String fallbackEvent, String fallbackEventForCircuitBreaker, EventType eventType, EventFormat eventFormat, LocalDateTime now, LocalDateTime localDateTime, String online, int i, int i1, EventStatus eventStatus, long l1, Object o, Object o1) {
    }
}


