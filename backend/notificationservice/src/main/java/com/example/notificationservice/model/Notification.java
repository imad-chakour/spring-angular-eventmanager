package com.example.notificationservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notif_seq")
    @SequenceGenerator(name = "notif_seq", sequenceName = "NOTIFICATIONS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "recipient_id")
    private Long recipientId;

    @Column(name = "recipient_email")
    private String recipientEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationChannel channel;

    @Column(name = "subject")
    private String subject;

    @Column(name = "content", length = 4000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private NotificationStatus status;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Column(name = "delivery_status")
    private String deliveryStatus;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.status = NotificationStatus.PENDING;
    }

    public Notification(long l, long l1, String mail, NotificationType notificationType, NotificationChannel notificationChannel, String fallbackNotification, String thisIsAFallbackNotification, NotificationStatus notificationStatus, Object o, String delivered, Object o1, int i, LocalDateTime now) {
    }
}