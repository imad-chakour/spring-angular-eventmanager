package com.example.event_service.service;

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
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Data
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private UserClient userClient;

    public Optional<Event> getEvent(final Long id) {
        return eventRepository.findById(id);
    }

    public Iterable<Event> getEvents() {
        return eventRepository.findAll();
    }

    public Iterable<Event> getEventsByOrganizer(Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }

    public Iterable<Event> getEventsByStatus(EventStatus status) {
        return eventRepository.findByStatus(status);
    }

    public void deleteEvent(final Long id) {
        eventRepository.findById(id).ifPresent(event -> {
            event.setStatus(EventStatus.ANNULE);
            event.setUpdatedAt(LocalDateTime.now());
            eventRepository.save(event);
        });
    }

    public Event saveEvent(Event event) {
        if (event.getId() == null) {
            event.setEventId("EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            event.setCreatedAt(LocalDateTime.now());
        }
        // Validate organizer through User Service
        if (event.getOrganizerId() != null) {
            Map<String, Object> organizer = userClient.getUserById(event.getOrganizerId());
            if (organizer == null || organizer.isEmpty()) {
                throw new RuntimeException("Organizer not found with id " + event.getOrganizerId());
            }
        }
        event.setUpdatedAt(LocalDateTime.now());
        return eventRepository.save(event);
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
        // Check if already registered
        registrationRepository.findByEventIdAndUserId(eventId, userId)
                .ifPresent(reg -> {
                    throw new RuntimeException("User already registered for this event");
                });

        // Validate event exists in current service
        eventRepository.findById(eventId).orElseThrow(() ->
                new RuntimeException("Event not found with id " + eventId));

        // Validate user through User Service
        Map<String, Object> user = userClient.getUserById(userId);
        if (user == null || user.isEmpty()) {
            throw new RuntimeException("User not found with id " + userId);
        }

        Registration registration = new Registration();
        registration.setEventId(eventId);
        registration.setUserId(userId);
        registration.setStatus(RegistrationStatus.PENDING);
        return saveRegistration(registration);
    }

    public void deleteRegistration(final Long id) {
        registrationRepository.deleteById(id);
    }
}
