package com.example.campaignservice.repositoy;

import com.example.campaignservice.model.Campaign;
import com.example.campaignservice.model.CampaignStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CampaignRepository extends CrudRepository<Campaign, Long> {
    List<Campaign> findByOrganizerId(Long organizerId);
    List<Campaign> findByStatus(CampaignStatus status);
}