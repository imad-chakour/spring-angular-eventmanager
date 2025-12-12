package com.example.notificationservice.batch;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemReaderBuilder;
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
            // Simulation du traitement d'envoi de notification
            // Dans un cas réel, ici on enverrait l'email/SMS/push notification
            System.out.println("Traitement de la notification ID: " + notification.getId());
            
            // Mise à jour du statut
            notification.setStatus(NotificationStatus.PROCESSING);
            
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
