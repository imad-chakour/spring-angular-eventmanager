package com.example.participant_service.controller;

import com.example.participant_service.model.Participant;
import com.example.participant_service.model.ParticipantStatus;
import com.example.participant_service.service.ParticipantService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/participants")
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    @GetMapping("/")
    public String home() {
        return "Participant Service is running!";
    }

    @Retry(name = "participantRetry", fallbackMethod = "fallbackParticipantsCB")
    @CircuitBreaker(name = "participantCB", fallbackMethod = "fallbackParticipantsCB")
    @GetMapping
    public Iterable<Participant> getParticipants() {
        simulateRandomFailure();
        return participantService.getParticipants();
    }

    @GetMapping("/{id}")
    public Participant getParticipant(@PathVariable("id") final Long id) {
        return participantService.getParticipant(id).orElse(null);
    }

    @GetMapping("/email/{email}")
    public Participant getParticipantByEmail(@PathVariable("email") final String email) {
        return participantService.getParticipantByEmail(email).orElse(null);
    }

    @PostMapping
    public Participant createParticipant(@RequestBody Participant participant) {
        return participantService.saveParticipant(participant);
    }

    @PutMapping("/{id}")
    public Participant updateParticipant(@PathVariable("id") final Long id, @RequestBody Participant participant) {
        return participantService.getParticipant(id).map(existing -> {
            existing.setFirstName(participant.getFirstName());
            existing.setLastName(participant.getLastName());
            existing.setEmail(participant.getEmail());
            existing.setPhone(participant.getPhone());
            existing.setCompany(participant.getCompany());
            existing.setJobTitle(participant.getJobTitle());
            existing.setStatus(participant.getStatus());
            existing.setSegments(participant.getSegments());
            existing.setCommunicationPreferences(participant.getCommunicationPreferences());
            existing.setOptInMarketing(participant.getOptInMarketing());
            return participantService.saveParticipant(existing);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteParticipant(@PathVariable("id") final Long id) {
        participantService.deleteParticipant(id);
    }

    @PatchMapping("/{id}/last-activity")
    public void updateLastActivity(@PathVariable("id") final Long id) {
        participantService.updateLastActivity(id);
    }

    private void simulateRandomFailure() {
        if (Math.random() < 0.3) {
            throw new RuntimeException("Simulated random failure in Participant Service");
        }
    }

    public Iterable<Participant> fallbackParticipantsCB(Exception e) {
        System.err.println("Participant Service Fallback: " + e.getMessage());
        return List.of(
                new Participant(1L, "fallback@eventflow.com", "Fallback", "Participant",
                        "+1234567890", "Fallback Corp", "Participant",
                        ParticipantStatus.ACTIVE, List.of("fallback-segment"),
                        List.of("email"), LocalDateTime.now(), LocalDateTime.now(), true)
        );
    }
}