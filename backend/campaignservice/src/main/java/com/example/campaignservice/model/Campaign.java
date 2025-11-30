package com.example.campaignservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "campaigns")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "camp_seq")
    @SequenceGenerator(name = "camp_seq", sequenceName = "CAMPAIGNS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "reference", unique = true, nullable = false)
    private String reference;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "budget")
    private Double budget;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CampaignStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private Channel channel;

    // Reference to User service (not a JPA relationship)
    @Column(name = "organizer_id", nullable = false)
    private Long organizerId;

    @ElementCollection
    @CollectionTable(name = "campaign_segments", joinColumns = @JoinColumn(name = "campaign_id"))
    @Column(name = "segment_name")
    private List<String> targetSegments = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Campaign() {
        this.createdAt = LocalDateTime.now();
        this.status = CampaignStatus.BROUILLON;
    }

    public Campaign(long l, String s, String fallbackCampaign, String fallbackCampaignForCircuitBreaker, LocalDateTime now, LocalDateTime localDateTime, double v, CampaignStatus campaignStatus, Channel channel, long l1, List<String> strings, Object o, Object o1) {
    }
}
