package com.example.event_service.controller;

import com.example.event_service.model.*;
import com.example.event_service.service.EventService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/")
    public String home() {
        return "Event Service is running!";
    }

    @Retry(name = "eventRetry", fallbackMethod = "fallbackEventsCB")
    @CircuitBreaker(name = "eventCB", fallbackMethod = "fallbackEventsCB")
    @GetMapping
    public Iterable<Event> getEvents() {
        simulateRandomFailure();
        return eventService.getEvents();
    }

    @GetMapping("/organizer/{organizerId}")
    public Iterable<Event> getEventsByOrganizer(@PathVariable("organizerId") final Long organizerId) {
        return eventService.getEventsByOrganizer(organizerId);
    }

    @GetMapping("/status/{status}")
    public Iterable<Event> getEventsByStatus(@PathVariable("status") final EventStatus status) {
        return eventService.getEventsByStatus(status);
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable("id") final Long id) {
        return eventService.getEvent(id).orElse(null);
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventService.saveEvent(event);
    }

    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable("id") final Long id, @RequestBody Event event) {
        return eventService.getEvent(id).map(existing -> {
            existing.setTitle(event.getTitle());
            existing.setDescription(event.getDescription());
            existing.setType(event.getType());
            existing.setFormat(event.getFormat());
            existing.setStartDate(event.getStartDate());
            existing.setEndDate(event.getEndDate());
            existing.setLocation(event.getLocation());
            existing.setMaxCapacity(event.getMaxCapacity());
            existing.setStatus(event.getStatus());
            return eventService.saveEvent(existing);
        }).orElse(null);
    }

    @PatchMapping("/{id}/close")
    public Event closeEvent(@PathVariable("id") final Long id) {
        return eventService.closeEvent(id);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable("id") final Long id) {
        eventService.deleteEvent(id);
    }

    // Registration endpoints
    @GetMapping("/registrations")
    public Iterable<Registration> getRegistrations() {
        return eventService.getRegistrations();
    }

    @GetMapping("/registrations/event/{eventId}")
    public Iterable<Registration> getRegistrationsByEvent(@PathVariable("eventId") final Long eventId) {
        return eventService.getRegistrationsByEvent(eventId);
    }

    @GetMapping("/registrations/participant/{participantId}")
    public Iterable<Registration> getRegistrationsByParticipant(@PathVariable("participantId") final Long participantId) {
        return eventService.getRegistrationsByParticipant(participantId);
    }

    @PostMapping("/registrations/event/{eventId}/participant/{participantId}")
    public Registration registerParticipant(
            @PathVariable("eventId") final Long eventId,
            @PathVariable("participantId") final Long participantId) {
        return eventService.registerParticipant(eventId, participantId);
    }

    @DeleteMapping("/registrations/{id}")
    public void deleteRegistration(@PathVariable("id") final Long id) {
        eventService.deleteRegistration(id);
    }

    private void simulateRandomFailure() {
        if (Math.random() < 0.3) {
            throw new RuntimeException("Simulated random failure in Event Service");
        }
    }

    public Iterable<Event> fallbackEventsCB(Exception e) {
        System.err.println("Event Service Fallback: " + e.getMessage());
        return List.of(
                new Event(1L, "EVT-FALLBACK", "Fallback Event", "Fallback event for circuit breaker",
                        EventType.WEBINAIRE, EventFormat.VIRTUEL, LocalDateTime.now(),
                        LocalDateTime.now().plusHours(2), "Online", 100, 0, EventStatus.PLANIFIED, 1L, null, null)
        );
    }
}