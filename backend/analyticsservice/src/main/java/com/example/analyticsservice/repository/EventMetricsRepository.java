package com.example.analyticsservice.repository;

import com.example.analyticsservice.model.EventMetrics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventMetricsRepository extends CrudRepository<EventMetrics, Long> {
    List<EventMetrics> findByEventId(Long eventId);
}