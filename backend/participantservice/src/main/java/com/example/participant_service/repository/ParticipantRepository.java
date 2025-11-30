package com.example.participant_service.repository;

import com.example.participant_service.model.Participant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends CrudRepository<Participant, Long> {
    Optional<Participant> findByEmail(String email);
    boolean existsByEmail(String email);
}