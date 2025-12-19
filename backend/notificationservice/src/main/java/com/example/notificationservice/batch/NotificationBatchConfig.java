package com.example.notificationservice.batch;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.EmailService;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration Spring Batch pour le traitement par lots des notifications
 * Permet de traiter les notifications en masse (envoi, mise à jour de statut, etc.)
 */
@Configuration
public class NotificationBatchConfig {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private EmailService emailService;

    /**
     * ItemReader : Lit les notifications en attente depuis la base de données
     */
    @Bean
    public ItemReader<Notification> notificationItemReader() {
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);

        return new RepositoryItemReaderBuilder<Notification>()
                .name("notificationItemReader")
                .repository(notificationRepository)
                .methodName("findByStatus")
                .arguments(NotificationStatus.PENDING)
                .sorts(sorts)
                .pageSize(100) // Traite 100 notifications par page
                .build();
    }

    /**
     * ItemProcessor : Traite chaque notification (simulation d'envoi)
     */
    @Bean
    public ItemProcessor<Notification, Notification> notificationItemProcessor() {
        return notification -> {
            System.out.println("=== Batch: Traitement de la notification ID: " + notification.getId() + " ===");
            
            // Envoyer l'email si le canal est EMAIL
            if (notification.getChannel() != null && 
                notification.getChannel().name().equals("EMAIL") && 
                notification.getRecipientEmail() != null && 
                !notification.getRecipientEmail().isEmpty()) {
                
                boolean emailSent = emailService.sendEmail(
                    notification.getRecipientEmail(),
                    notification.getSubject() != null ? notification.getSubject() : "Notification EventFlow",
                    notification.getContent() != null ? notification.getContent() : ""
                );
                
                if (emailSent) {
                    notification.setStatus(NotificationStatus.SENT);
                    notification.setSentDate(java.time.LocalDateTime.now());
                    notification.setDeliveryStatus("DELIVERED");
                    System.out.println("✅ Email envoyé via batch pour notification ID: " + notification.getId());
                } else {
                    // En cas d'échec, incrémenter le retry count
                    notification.setRetryCount(notification.getRetryCount() != null ? notification.getRetryCount() + 1 : 1);
                    if (notification.getRetryCount() >= 3) {
                        notification.setStatus(NotificationStatus.FAILED);
                        notification.setErrorMessage("Échec après 3 tentatives");
                    }
                    System.out.println("⚠️ Échec de l'envoi, retry count: " + notification.getRetryCount());
                }
            } else {
                // Pour les autres canaux (SMS, PUSH, etc.), on simule juste l'envoi
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentDate(java.time.LocalDateTime.now());
                System.out.println("✅ Notification traitée (canal non-EMAIL): " + notification.getId());
            }
            
            return notification;
        };
    }

    /**
     * ItemWriter : Sauvegarde les notifications traitées
     */
    @Bean
    public ItemWriter<Notification> notificationItemWriter() {
        return notifications -> {
            for (Notification notification : notifications) {
                notificationRepository.save(notification);
            }
        };
    }

    /**
     * Step : Définit une étape de traitement
     */
    @Bean
    public Step processNotificationStep() {
        return new StepBuilder("processNotificationStep", jobRepository)
                .<Notification, Notification>chunk(10, transactionManager) // Traite par lots de 10
                .reader(notificationItemReader())
                .processor(notificationItemProcessor())
                .writer(notificationItemWriter())
                .build();
    }

    /**
     * Job : Définit le job de traitement des notifications
     */
    @Bean
    public Job processNotificationJob() {
        return new JobBuilder("processNotificationJob", jobRepository)
                .start(processNotificationStep())
                .build();
    }
}
