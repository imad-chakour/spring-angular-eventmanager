package com.example.analyticsservice.repository;

import com.example.analyticsservice.model.CampaignMetrics;
import com.example.analyticsservice.model.EventMetrics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CampaignMetricsRepository extends CrudRepository<CampaignMetrics, Long> {
    List<CampaignMetrics> findByCampaignId(Long campaignId);
}