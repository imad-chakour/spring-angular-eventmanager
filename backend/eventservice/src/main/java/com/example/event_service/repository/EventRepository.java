package com.example.event_service.repository;

import com.example.event_service.model.Event;
import com.example.event_service.model.EventStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    List<Event> findByOrganizerId(Long organizerId);
    List<Event> findByStatus(EventStatus status);
}
