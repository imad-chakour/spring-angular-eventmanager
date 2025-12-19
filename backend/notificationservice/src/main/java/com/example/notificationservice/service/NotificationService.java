package com.example.notificationservice.service;

import com.example.notificationservice.client.CampaignClient;
import com.example.notificationservice.client.EventClient;
import com.example.notificationservice.client.UserClient;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Data
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private EventClient eventClient;

    @Autowired
    private CampaignClient campaignClient;

    @Autowired
    private EmailService emailService;

    public Optional<Notification> getNotification(final Long id) {
        return notificationRepository.findById(id);
    }

    public Iterable<Notification> getNotifications() {
        return notificationRepository.findAll();
    }

    public Iterable<Notification> getNotificationsByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status);
    }

    public Iterable<Notification> getNotificationsByRecipient(Long recipientId) {
        return notificationRepository.findByRecipientId(recipientId);
    }

    public void deleteNotification(final Long id) {
        notificationRepository.deleteById(id);
    }

    public Notification saveNotification(Notification notification) {
        // S'assurer que tous les champs obligatoires sont définis
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }
        if (notification.getStatus() == null) {
            notification.setStatus(NotificationStatus.PENDING);
        }
        if (notification.getRetryCount() == null) {
            notification.setRetryCount(0);
        }
        
        // Validation des champs obligatoires
        if (notification.getType() == null) {
            throw new IllegalArgumentException("Le champ 'type' est obligatoire et ne peut pas être null");
        }
        if (notification.getChannel() == null) {
            throw new IllegalArgumentException("Le champ 'channel' est obligatoire et ne peut pas être null");
        }
        
        System.out.println("=== NotificationService.saveNotification ===");
        System.out.println("Type: " + notification.getType());
        System.out.println("Channel: " + notification.getChannel());
        System.out.println("Status: " + notification.getStatus());
        System.out.println("RetryCount: " + notification.getRetryCount());
        System.out.println("CreatedAt: " + notification.getCreatedAt());
        // Validate recipient if it's a user
        // Note: La validation de l'utilisateur est optionnelle pour éviter les erreurs 403
        // si le token JWT n'est pas disponible dans le contexte Feign
        if (notification.getRecipientId() != null) {
            try {
                Map<String, Object> user = userClient.getUserById(notification.getRecipientId());
                if (user == null || user.isEmpty()) {
                    System.out.println("⚠️ User not found with id " + notification.getRecipientId() + ", but continuing notification creation");
                } else {
                    System.out.println("✅ User validated: " + user.get("email"));
                }
            } catch (Exception e) {
                // Ne pas faire échouer la création de notification si la validation échoue
                // (peut arriver si le token JWT n'est pas disponible dans le contexte Feign)
                System.err.println("⚠️ Erreur lors de la validation de l'utilisateur: " + e.getMessage());
                System.err.println("   Continuation de la création de la notification sans validation");
            }
        }
        Notification saved = notificationRepository.save(notification);
        
        // Envoyer l'email immédiatement si le canal est EMAIL
        if (saved.getChannel() != null && saved.getChannel().name().equals("EMAIL") && 
            saved.getRecipientEmail() != null && !saved.getRecipientEmail().isEmpty()) {
            try {
                boolean emailSent = emailService.sendEmail(
                    saved.getRecipientEmail(),
                    saved.getSubject() != null ? saved.getSubject() : "Notification EventFlow",
                    saved.getContent() != null ? saved.getContent() : ""
                );
                
                if (emailSent) {
                    // Mettre à jour le statut à SENT et enregistrer la date d'envoi
                    saved.setStatus(NotificationStatus.SENT);
                    saved.setSentDate(LocalDateTime.now());
                    saved.setDeliveryStatus("DELIVERED");
                    saved = notificationRepository.save(saved);
                    System.out.println("✅ Email envoyé et notification mise à jour: " + saved.getId());
                } else {
                    // En cas d'échec, garder le statut PENDING pour retry
                    System.out.println("⚠️ Échec de l'envoi de l'email, notification reste en PENDING: " + saved.getId());
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de l'envoi de l'email: " + e.getMessage());
                // Garder le statut PENDING pour retry ultérieur
            }
        }
        
        return saved;
    }

    public Notification updateNotificationStatus(Long id, NotificationStatus status) {
        return notificationRepository.findById(id).map(notification -> {
            notification.setStatus(status);
            if (status == NotificationStatus.SENT) {
                notification.setSentDate(LocalDateTime.now());
            }
            return notificationRepository.save(notification);
        }).orElse(null);
    }
}