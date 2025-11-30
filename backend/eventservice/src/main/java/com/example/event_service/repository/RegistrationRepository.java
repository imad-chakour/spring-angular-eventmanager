package com.example.event_service.repository;

import com.example.event_service.model.Registration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends CrudRepository<Registration, Long> {
    List<Registration> findByEventId(Long eventId);
    List<Registration> findByParticipantId(Long participantId);
    Optional<Registration> findByEventIdAndParticipantId(Long eventId, Long participantId);
}
