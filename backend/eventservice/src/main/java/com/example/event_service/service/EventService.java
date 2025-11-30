package com.example.event_service.service;

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
import java.util.Optional;
import java.util.UUID;

@Data
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

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

    public Iterable<Registration> getRegistrationsByParticipant(Long participantId) {
        return registrationRepository.findByParticipantId(participantId);
    }

    public Registration saveRegistration(Registration registration) {
        registration.setRegistrationDate(LocalDateTime.now());
        return registrationRepository.save(registration);
    }

    public Registration registerParticipant(Long eventId, Long participantId) {
        // Check if already registered
        registrationRepository.findByEventIdAndParticipantId(eventId, participantId)
                .ifPresent(reg -> {
                    throw new RuntimeException("Participant already registered for this event");
                });

        Registration registration = new Registration();
        registration.setEventId(eventId);
        registration.setParticipantId(participantId);
        registration.setStatus(RegistrationStatus.PENDING);
        return saveRegistration(registration);
    }

    public void deleteRegistration(final Long id) {
        registrationRepository.deleteById(id);
    }
}
