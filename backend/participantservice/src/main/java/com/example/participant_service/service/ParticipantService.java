package com.example.participant_service.service;

import com.example.participant_service.model.Participant;
import com.example.participant_service.model.ParticipantStatus;
import com.example.participant_service.repository.ParticipantRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    public Optional<Participant> getParticipant(final Long id) {
        return participantRepository.findById(id);
    }

    public Iterable<Participant> getParticipants() {
        return participantRepository.findAll();
    }

    public void deleteParticipant(final Long id) {
        participantRepository.findById(id).ifPresent(participant -> {
            participant.setStatus(ParticipantStatus.INACTIVE);
            participant.setLastActivity(LocalDateTime.now());
            participantRepository.save(participant);
        });
    }

    public Participant saveParticipant(Participant participant) {
        if (participant.getId() == null) {
            participant.setRegistrationDate(LocalDateTime.now());
        }
        participant.setLastActivity(LocalDateTime.now());
        return participantRepository.save(participant);
    }

    public Optional<Participant> getParticipantByEmail(String email) {
        return participantRepository.findByEmail(email);
    }

    public void updateLastActivity(Long id) {
        participantRepository.findById(id).ifPresent(participant -> {
            participant.setLastActivity(LocalDateTime.now());
            participantRepository.save(participant);
        });
    }
}