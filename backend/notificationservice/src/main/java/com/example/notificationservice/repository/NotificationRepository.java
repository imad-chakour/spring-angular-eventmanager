package com.example.notificationservice.repository;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.model.NotificationStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
    List<Notification> findByStatus(NotificationStatus status);
    List<Notification> findByRecipientId(Long recipientId);
}