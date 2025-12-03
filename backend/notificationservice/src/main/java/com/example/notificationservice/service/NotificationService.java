package com.example.notificationservice.service;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

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
        notification.setCreatedAt(LocalDateTime.now());
        if (notification.getStatus() == null) {
            notification.setStatus(NotificationStatus.PENDING);
        }
        return notificationRepository.save(notification);
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