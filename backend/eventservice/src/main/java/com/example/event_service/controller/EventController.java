package com.example.event_service.controller;

import com.example.event_service.model.*;
import com.example.event_service.service.EventService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    public ResponseEntity<List<Event>> getEvents() {
        // Temporarily disabled for debugging
        // simulateRandomFailure();
        System.out.println("=== EventController: getEvents ===");
        try {
            Iterable<Event> events = eventService.getEvents();
            System.out.println("Events from service (Iterable): " + events);
            List<Event> eventList = StreamSupport.stream(events.spliterator(), false)
                    .collect(Collectors.toList());
            System.out.println("Events converted to List, count: " + eventList.size());
            if (!eventList.isEmpty()) {
                System.out.println("First event: " + eventList.get(0));
            } else {
                System.out.println("⚠️ WARNING: Event list is empty - database might be empty or connection issue");
            }
            System.out.println("Returning ResponseEntity with list size: " + eventList.size());
            return ResponseEntity.ok(eventList);
        } catch (Exception e) {
            System.err.println("❌ ERROR in getEvents: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(List.of());
        }
    }

    @GetMapping("/organizer/{organizerId}")
    public List<Event> getEventsByOrganizer(@PathVariable("organizerId") final Long organizerId) {
        Iterable<Event> events = eventService.getEventsByOrganizer(organizerId);
        return StreamSupport.stream(events.spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/status/{status}")
    public List<Event> getEventsByStatus(@PathVariable("status") final EventStatus status) {
        Iterable<Event> events = eventService.getEventsByStatus(status);
        return StreamSupport.stream(events.spliterator(), false)
                .collect(Collectors.toList());
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
    public List<Registration> getRegistrations() {
        Iterable<Registration> registrations = eventService.getRegistrations();
        return StreamSupport.stream(registrations.spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/registrations/event/{eventId}")
    public List<Registration> getRegistrationsByEvent(@PathVariable("eventId") final Long eventId) {
        Iterable<Registration> registrations = eventService.getRegistrationsByEvent(eventId);
        return StreamSupport.stream(registrations.spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/registrations/user/{userId}")
    public List<Registration> getRegistrationsByUser(@PathVariable("userId") final Long userId) {
        Iterable<Registration> registrations = eventService.getRegistrationsByUser(userId);
        return StreamSupport.stream(registrations.spliterator(), false)
                .collect(Collectors.toList());
    }

    @PostMapping("/registrations/event/{eventId}/user/{userId}")
    public Registration registerUser(
            @PathVariable("eventId") final Long eventId,
            @PathVariable("userId") final Long userId) {
        return eventService.registerUser(eventId, userId);
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