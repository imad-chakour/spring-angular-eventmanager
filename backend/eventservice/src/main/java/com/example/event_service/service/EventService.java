package com.example.event_service.service;

import com.example.event_service.client.NotificationClient;
import com.example.event_service.client.UserClient;
import com.example.event_service.model.Event;
import com.example.event_service.model.EventStatus;
import com.example.event_service.model.Registration;
import com.example.event_service.model.RegistrationStatus;
import com.example.event_service.repository.EventRepository;
import com.example.event_service.repository.RegistrationRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Data
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private NotificationClient notificationClient;

    public Optional<Event> getEvent(final Long id) {
        return eventRepository.findById(id);
    }

    public Iterable<Event> getEvents() {
        System.out.println("=== EventService: getEvents ===");
        try {
            System.out.println("Repository instance: " + eventRepository);
            Iterable<Event> events = eventRepository.findAll();
            System.out.println("Events from repository (Iterable): " + events);
            // Count events
            long count = StreamSupport.stream(events.spliterator(), false).count();
            System.out.println("Total events count: " + count);
            
            // Log first few events if any
            if (count > 0) {
                System.out.println("First event details:");
                events.forEach(event -> {
                    System.out.println("  - Event ID: " + event.getId() + ", Title: " + event.getTitle());
                });
            } else {
                System.out.println("⚠️ WARNING: No events found in database. Database might be empty or connection issue.");
            }
            
            return events;
        } catch (Exception e) {
            System.err.println("❌ ERROR in EventService.getEvents: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Iterable<Event> getEventsByOrganizer(Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }

    public Iterable<Event> getEventsByStatus(EventStatus status) {
        return eventRepository.findByStatus(status);
    }

    @Transactional
    public void deleteEvent(final Long id) {
        System.out.println("=== EventService.deleteEvent() appelé ===");
        System.out.println("Event ID: " + id);
        
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            System.out.println("Event trouvé - ID: " + event.getId() + ", Title: " + event.getTitle() + ", Status: " + event.getStatus());
            
            // Supprimer d'abord les inscriptions associées
            Iterable<Registration> registrations = registrationRepository.findByEventId(id);
            long regCount = StreamSupport.stream(registrations.spliterator(), false).count();
            System.out.println("  - Nombre d'inscriptions à supprimer: " + regCount);
            
            if (regCount > 0) {
                registrationRepository.deleteByEventId(id);
                System.out.println("✅ Inscriptions supprimées");
            }
            
            // Suppression réelle de la base de données (hard delete)
            eventRepository.deleteById(id);
            System.out.println("✅ Event supprimé définitivement de la base de données - ID: " + id);
        } else {
            System.err.println("⚠️ Event non trouvé avec ID: " + id);
            throw new IllegalArgumentException("Event not found with id: " + id);
        }
    }

    public Event saveEvent(Event event) {
        System.out.println("=== EventService.saveEvent() appelé ===");
        
        // Validation des champs obligatoires
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre de l'événement est obligatoire");
        }
        if (event.getStartDate() == null) {
            throw new IllegalArgumentException("La date de début est obligatoire");
        }
        if (event.getEndDate() == null) {
            throw new IllegalArgumentException("La date de fin est obligatoire");
        }
        if (event.getEndDate().isBefore(event.getStartDate())) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }
        if (event.getOrganizerId() == null) {
            throw new IllegalArgumentException("L'ID de l'organisateur est obligatoire");
        }
        if (event.getType() == null) {
            throw new IllegalArgumentException("Le type d'événement est obligatoire");
        }
        
        // Nouvel événement
        if (event.getId() == null) {
            // Générer un eventId unique
            String eventId = "EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            event.setEventId(eventId);
            event.setCreatedAt(LocalDateTime.now());
            System.out.println("  Nouvel événement - EventId généré: " + eventId);
            
            // Définir le statut par défaut si non fourni
            if (event.getStatus() == null) {
                event.setStatus(EventStatus.PLANIFIED);
                System.out.println("  Statut par défaut défini: PLANIFIED");
            }
            
            // Initialiser currentParticipants à 0
            if (event.getCurrentParticipants() == null) {
                event.setCurrentParticipants(0);
            }
        } else {
            System.out.println("  Mise à jour de l'événement ID: " + event.getId());
        }
        
        // Validate organizer through User Service
        // Note: La validation est optionnelle pour éviter les erreurs 403 si le token JWT n'est pas disponible
        if (event.getOrganizerId() != null) {
            try {
                Map<String, Object> organizer = userClient.getUserById(event.getOrganizerId());
                if (organizer == null || organizer.isEmpty()) {
                    System.out.println("⚠️ Organizer not found with id " + event.getOrganizerId() + ", but continuing event creation");
                } else {
                    System.out.println("✅ Organizer validated: " + organizer.get("email"));
                }
            } catch (Exception e) {
                // Ne pas faire échouer la création d'événement si la validation échoue
                System.err.println("⚠️ Erreur lors de la validation de l'organizer: " + e.getMessage());
                System.err.println("   Continuation de la création de l'événement sans validation");
            }
        }
        
        event.setUpdatedAt(LocalDateTime.now());
        
        try {
            Event saved = eventRepository.save(event);
            System.out.println("✅ Event sauvegardé - ID: " + saved.getId() + ", EventId: " + saved.getEventId());
            return saved;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la sauvegarde de l'événement:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "N/A"));
            e.printStackTrace();
            throw e;
        }
    }

    public Event closeEvent(Long id) {
        return eventRepository.findById(id).map(event -> {
            event.setStatus(EventStatus.CLOTURE);
            event.setUpdatedAt(LocalDateTime.now());
            return eventRepository.save(event);
        }).orElse(null);
    }

    // Registration methods
    public Optional<Registration> getRegistration(final Long id) {
        return registrationRepository.findById(id);
    }

    public Iterable<Registration> getRegistrations() {
        return registrationRepository.findAll();
    }

    public Iterable<Registration> getRegistrationsByEvent(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    public Iterable<Registration> getRegistrationsByUser(Long userId) {
        return registrationRepository.findByUserId(userId);
    }

    public Registration saveRegistration(Registration registration) {
        registration.setRegistrationDate(LocalDateTime.now());
        return registrationRepository.save(registration);
    }

    public Registration registerUser(Long eventId, Long userId) {
        System.out.println("=== EventService: registerUser ===");
        System.out.println("Event ID: " + eventId + ", User ID: " + userId);
        
        // Check if already registered
        registrationRepository.findByEventIdAndUserId(eventId, userId)
                .ifPresent(reg -> {
                    System.out.println("User already registered for this event");
                    throw new RuntimeException("User already registered for this event");
                });

        // Validate event exists in current service
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new RuntimeException("Event not found with id " + eventId));
        
        System.out.println("Event found: " + event.getTitle());
        
        // Check capacity
        if (event.getMaxCapacity() != null && event.getCurrentParticipants() != null) {
            if (event.getCurrentParticipants() >= event.getMaxCapacity()) {
                System.out.println("Event is full: " + event.getCurrentParticipants() + "/" + event.getMaxCapacity());
                throw new RuntimeException("Event is full. No more registrations allowed.");
            }
        }

        // Validate user through User Service
        Map<String, Object> user = userClient.getUserById(userId);
        if (user == null || user.isEmpty()) {
            System.out.println("User not found: " + userId);
            throw new RuntimeException("User not found with id " + userId);
        }
        
        System.out.println("User found: " + user.get("email"));

        // Create registration
        Registration registration = new Registration();
        registration.setEventId(eventId);
        registration.setUserId(userId);
        registration.setStatus(RegistrationStatus.PENDING);
        Registration saved = saveRegistration(registration);
        
        // Update event participant count
        if (event.getCurrentParticipants() == null) {
            event.setCurrentParticipants(0);
        }
        event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        eventRepository.save(event);
        
        System.out.println("Registration saved. Updated participant count: " + event.getCurrentParticipants());
        
        // Créer une notification de confirmation d'inscription
        try {
            Map<String, Object> userInfo = user;
            String userEmail = (String) userInfo.get("email");
            String firstName = (String) userInfo.get("firstName");
            
            Map<String, Object> registrationNotification = new HashMap<>();
            registrationNotification.put("recipientId", userId);
            registrationNotification.put("recipientEmail", userEmail != null ? userEmail : "user@example.com");
            registrationNotification.put("type", "EVENT_REGISTRATION");
            registrationNotification.put("channel", "EMAIL");
            registrationNotification.put("subject", "Confirmation d'inscription - " + event.getTitle());
            registrationNotification.put("content", "Bonjour " + (firstName != null ? firstName : "Utilisateur") + 
                    ",\n\nVotre inscription à l'événement \"" + event.getTitle() + "\" a été confirmée.\n\n" +
                    "Détails de l'événement :\n" +
                    "- Date de début : " + event.getStartDate() + "\n" +
                    "- Date de fin : " + event.getEndDate() + "\n" +
                    "- Lieu : " + (event.getLocation() != null ? event.getLocation() : "En ligne") + "\n\n" +
                    "Nous avons hâte de vous voir !\n\n" +
                    "Cordialement,\nL'équipe EventFlow");
            registrationNotification.put("status", "PENDING");
            
            notificationClient.createNotification(registrationNotification);
            System.out.println("=== Notification d'inscription créée pour l'utilisateur: " + userId + " à l'événement: " + event.getTitle() + " ===");
        } catch (Exception e) {
            // Ne pas faire échouer l'inscription si la notification échoue
            System.err.println("⚠️ Erreur lors de la création de la notification d'inscription: " + e.getMessage());
        }
        
        return saved;
    }

    public void deleteRegistration(final Long id) {
        registrationRepository.deleteById(id);
    }
}
