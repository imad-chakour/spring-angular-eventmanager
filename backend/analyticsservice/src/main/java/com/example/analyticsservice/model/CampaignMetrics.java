package com.example.analyticsservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "campaign_metrics")
public class CampaignMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "metric_seq")
    @SequenceGenerator(name = "metric_seq", sequenceName = "METRICS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;

    @Column(name = "campaign_reference")
    private String campaignReference;

    @Column(name = "emails_sent")
    private Integer emailsSent = 0;

    @Column(name = "emails_delivered")
    private Integer emailsDelivered = 0;

    @Column(name = "emails_opened")
    private Integer emailsOpened = 0;

    @Column(name = "clicks")
    private Integer clicks = 0;

    @Column(name = "conversions")
    private Integer conversions = 0;

    @Column(name = "bounce_rate")
    private Double bounceRate = 0.0;

    @Column(name = "open_rate")
    private Double openRate = 0.0;

    @Column(name = "click_rate")
    private Double clickRate = 0.0;

    @Column(name = "conversion_rate")
    private Double conversionRate = 0.0;

    @Column(name = "calculation_date")
    private LocalDateTime calculationDate;

    public CampaignMetrics() {
        this.calculationDate = LocalDateTime.now();
    }

    public CampaignMetrics(long l, long l1, String s, int i, int i1, int i2, int i3, int i4, double v, double v1, double v2, double v3, LocalDateTime now) {
    }
}

