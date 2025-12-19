package com.example.event_service.repository;

import com.example.event_service.model.Registration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends CrudRepository<Registration, Long> {
    List<Registration> findByEventId(Long eventId);
    List<Registration> findByUserId(Long userId);  // Changed from findByParticipantId
    Optional<Registration> findByEventIdAndUserId(Long eventId, Long userId);  // Changed from findByEventIdAndParticipantId
    
    // Supprimer toutes les inscriptions d'un événement
    void deleteByEventId(Long eventId);
}
