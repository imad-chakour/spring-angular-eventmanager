package com.example.notificationservice.controller;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.model.NotificationChannel;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.model.NotificationType;
import com.example.notificationservice.service.NotificationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.notificationservice.model.NotificationType;
import com.example.notificationservice.model.NotificationChannel;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("processNotificationJob")
    private Job processNotificationJob;

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

    /**
     * Endpoint pour créer une notification depuis un Map (utilisé par Feign)
     */
    @PostMapping("/from-map")
    public Map<String, Object> createNotificationFromMap(@RequestBody Map<String, Object> notificationMap) {
        try {
            Notification notification = new Notification();
            
            // Champs optionnels
            if (notificationMap.get("recipientId") != null) {
                notification.setRecipientId(Long.valueOf(notificationMap.get("recipientId").toString()));
            }
            if (notificationMap.get("recipientEmail") != null) {
                notification.setRecipientEmail(notificationMap.get("recipientEmail").toString());
            }
            if (notificationMap.get("subject") != null) {
                notification.setSubject(notificationMap.get("subject").toString());
            }
            if (notificationMap.get("content") != null) {
                notification.setContent(notificationMap.get("content").toString());
            }
            if (notificationMap.get("status") != null) {
                notification.setStatus(NotificationStatus.valueOf(notificationMap.get("status").toString()));
            }
            
            // Champs OBLIGATOIRES (nullable = false dans la base)
            if (notificationMap.get("type") == null) {
                throw new IllegalArgumentException("Le champ 'type' est obligatoire");
            }
            notification.setType(NotificationType.valueOf(notificationMap.get("type").toString()));
            
            if (notificationMap.get("channel") == null) {
                throw new IllegalArgumentException("Le champ 'channel' est obligatoire");
            }
            notification.setChannel(NotificationChannel.valueOf(notificationMap.get("channel").toString()));
            
            // S'assurer que retryCount n'est pas null
            if (notification.getRetryCount() == null) {
                notification.setRetryCount(0);
            }
            
            // Log des valeurs avant sauvegarde pour debugging
            System.out.println("=== Création de notification ===");
            System.out.println("Type: " + notification.getType());
            System.out.println("Channel: " + notification.getChannel());
            System.out.println("Status: " + notification.getStatus());
            System.out.println("RetryCount: " + notification.getRetryCount());
            System.out.println("RecipientId: " + notification.getRecipientId());
            System.out.println("RecipientEmail: " + notification.getRecipientEmail());
            
            Notification saved = notificationService.saveNotification(notification);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("recipientId", saved.getRecipientId());
            response.put("recipientEmail", saved.getRecipientEmail());
            response.put("type", saved.getType());
            response.put("channel", saved.getChannel());
            response.put("subject", saved.getSubject());
            response.put("status", saved.getStatus());
            response.put("message", "Notification créée avec succès");
            
            System.out.println("=== Notification créée depuis Map: " + saved.getId() + " ===");
            return response;
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Erreur de validation: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur de validation: " + e.getMessage());
            error.put("message", "Vérifiez que les champs obligatoires 'type' et 'channel' sont présents et valides");
            return error;
        } catch (IllegalStateException e) {
            System.err.println("❌ Erreur d'enum: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur de valeur enum: " + e.getMessage());
            error.put("message", "Les valeurs de 'type', 'channel' ou 'status' ne sont pas valides");
            return error;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création de la notification depuis Map: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la création de la notification: " + e.getMessage());
            error.put("message", "Vérifiez les logs pour plus de détails");
            return error;
        }
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

    /**
     * Endpoint pour déclencher le job Spring Batch de traitement des notifications
     * POST /api/notifications/batch/process
     */
    @PostMapping("/batch/process")
    public ResponseEntity<Map<String, String>> triggerBatchProcessing() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(processNotificationJob, jobParameters);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Batch job 'processNotificationJob' has been triggered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to trigger batch job: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
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