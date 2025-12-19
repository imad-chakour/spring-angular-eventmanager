package com.example.event_service.controller;

import com.example.event_service.dto.EventCreateRequest;
import com.example.event_service.model.*;
import com.example.event_service.service.EventService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<?> createEvent(@RequestBody EventCreateRequest request) {
        System.out.println("=== EventController.createEvent() appelé ===");
        System.out.println("EventCreateRequest reçue:");
        System.out.println("  - Title: " + request.getTitle());
        System.out.println("  - Description: " + request.getDescription());
        System.out.println("  - Type: " + request.getType());
        System.out.println("  - Format: " + request.getFormat());
        System.out.println("  - StartDate: " + request.getStartDate());
        System.out.println("  - EndDate: " + request.getEndDate());
        System.out.println("  - Location: " + request.getLocation());
        System.out.println("  - MaxCapacity: " + request.getMaxCapacity());
        System.out.println("  - Status: " + request.getStatus());
        System.out.println("  - OrganizerId: " + request.getOrganizerId());
        
        try {
            // Convertir le DTO en entité Event
            Event event = new Event();
            event.setTitle(request.getTitle());
            event.setDescription(request.getDescription());
            event.setType(request.getType());
            event.setFormat(request.getFormat());
            
            // Parser les dates depuis les strings
            LocalDateTime startDate = request.getStartDateAsLocalDateTime();
            LocalDateTime endDate = request.getEndDateAsLocalDateTime();
            
            System.out.println("  - StartDate parsée: " + startDate);
            System.out.println("  - EndDate parsée: " + endDate);
            
            event.setStartDate(startDate);
            event.setEndDate(endDate);
            event.setLocation(request.getLocation());
            event.setMaxCapacity(request.getMaxCapacity());
            event.setStatus(request.getStatus() != null ? request.getStatus() : EventStatus.PLANIFIED);
            event.setOrganizerId(request.getOrganizerId());
            event.setCurrentParticipants(0);
            
            Event saved = eventService.saveEvent(event);
            System.out.println("✅ Event créé avec succès - ID: " + saved.getId() + ", EventId: " + saved.getEventId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Erreur de validation: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validation error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création de l'événement:");
            System.err.println("   Message: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable("id") final Long id, @RequestBody EventCreateRequest request) {
        System.out.println("=== EventController.updateEvent() appelé ===");
        System.out.println("Event ID: " + id);
        
        return eventService.getEvent(id).map(existing -> {
            System.out.println("Event existant trouvé - ID: " + existing.getId() + ", EventId: " + existing.getEventId());
            
            existing.setTitle(request.getTitle());
            existing.setDescription(request.getDescription());
            existing.setType(request.getType());
            existing.setFormat(request.getFormat());
            
            // Parser les dates depuis les strings
            if (request.getStartDate() != null) {
                LocalDateTime startDate = request.getStartDateAsLocalDateTime();
                System.out.println("  StartDate parsée: " + startDate);
                existing.setStartDate(startDate);
            }
            if (request.getEndDate() != null) {
                LocalDateTime endDate = request.getEndDateAsLocalDateTime();
                System.out.println("  EndDate parsée: " + endDate);
                existing.setEndDate(endDate);
            }
            
            existing.setLocation(request.getLocation());
            existing.setMaxCapacity(request.getMaxCapacity());
            if (request.getStatus() != null) {
                existing.setStatus(request.getStatus());
            }
            
            try {
                Event saved = eventService.saveEvent(existing);
                System.out.println("✅ Event mis à jour avec succès - ID: " + saved.getId());
                return ResponseEntity.ok(saved);
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la mise à jour de l'événement:");
                System.err.println("   Message: " + e.getMessage());
                e.printStackTrace();
                Map<String, String> error = new HashMap<>();
                error.put("error", "Internal server error");
                error.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/close")
    public Event closeEvent(@PathVariable("id") final Long id) {
        return eventService.closeEvent(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable("id") final Long id) {
        System.out.println("=== EventController.deleteEvent() appelé ===");
        System.out.println("Event ID à supprimer: " + id);
        
        try {
            eventService.deleteEvent(id);
            System.out.println("✅ Event supprimé définitivement avec succès - ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Erreur de validation: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Not found");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression de l'événement:");
            System.err.println("   Message: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
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