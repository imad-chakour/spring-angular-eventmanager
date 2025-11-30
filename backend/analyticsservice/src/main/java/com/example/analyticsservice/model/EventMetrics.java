package com.example.analyticsservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "event_metrics")
public class EventMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_metric_seq")
    @SequenceGenerator(name = "event_metric_seq", sequenceName = "EVENT_METRICS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "total_registrations")
    private Integer totalRegistrations = 0;

    @Column(name = "confirmed_registrations")
    private Integer confirmedRegistrations = 0;

    @Column(name = "actual_attendance")
    private Integer actualAttendance = 0;

    @Column(name = "attendance_rate")
    private Double attendanceRate = 0.0;

    @Column(name = "cancellation_rate")
    private Double cancellationRate = 0.0;

    @Column(name = "satisfaction_score")
    private Double satisfactionScore = 0.0;

    @Column(name = "calculation_date")
    private LocalDateTime calculationDate;

    public EventMetrics() {
        this.calculationDate = LocalDateTime.now();
    }

    public EventMetrics(long l, long l1, int i, int i1, int i2, double v, double v1, double v2, LocalDateTime now) {
    }
}