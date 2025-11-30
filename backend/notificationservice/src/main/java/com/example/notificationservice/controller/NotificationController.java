package com.example.notificationservice.controller;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.model.NotificationChannel;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.model.NotificationType;
import com.example.notificationservice.service.NotificationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/")
    public String home() {
        return "Notification Service is running!";
    }

    @Retry(name = "notificationRetry", fallbackMethod = "fallbackNotificationsCB")
    @CircuitBreaker(name = "notificationCB", fallbackMethod = "fallbackNotificationsCB")
    @GetMapping
    public Iterable<Notification> getNotifications() {
        simulateRandomFailure();
        return notificationService.getNotifications();
    }

    @GetMapping("/status/{status}")
    public Iterable<Notification> getNotificationsByStatus(@PathVariable("status") final NotificationStatus status) {
        return notificationService.getNotificationsByStatus(status);
    }

    @GetMapping("/recipient/{recipientId}")
    public Iterable<Notification> getNotificationsByRecipient(@PathVariable("recipientId") final Long recipientId) {
        return notificationService.getNotificationsByRecipient(recipientId);
    }

    @GetMapping("/{id}")
    public Notification getNotification(@PathVariable("id") final Long id) {
        return notificationService.getNotification(id).orElse(null);
    }

    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.saveNotification(notification);
    }

    @PutMapping("/{id}")
    public Notification updateNotification(@PathVariable("id") final Long id, @RequestBody Notification notification) {
        return notificationService.getNotification(id).map(existing -> {
            existing.setRecipientId(notification.getRecipientId());
            existing.setRecipientEmail(notification.getRecipientEmail());
            existing.setType(notification.getType());
            existing.setChannel(notification.getChannel());
            existing.setSubject(notification.getSubject());
            existing.setContent(notification.getContent());
            existing.setStatus(notification.getStatus());
            return notificationService.saveNotification(existing);
        }).orElse(null);
    }

    @PatchMapping("/{id}/status/{status}")
    public Notification updateNotificationStatus(@PathVariable("id") final Long id, @PathVariable("status") final NotificationStatus status) {
        return notificationService.updateNotificationStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable("id") final Long id) {
        notificationService.deleteNotification(id);
    }

    private void simulateRandomFailure() {
        if (Math.random() < 0.3) {
            throw new RuntimeException("Simulated random failure in Notification Service");
        }
    }

    public Iterable<Notification> fallbackNotificationsCB(Exception e) {
        System.err.println("Notification Service Fallback: " + e.getMessage());
        return List.of(
                new Notification(1L, 1L, "fallback@eventflow.com", NotificationType.SYSTEM_ALERT,
                        NotificationChannel.EMAIL, "Fallback Notification",
                        "This is a fallback notification", NotificationStatus.PENDING,
                        null, "DELIVERED", null, 0, LocalDateTime.now())
        );
    }
}