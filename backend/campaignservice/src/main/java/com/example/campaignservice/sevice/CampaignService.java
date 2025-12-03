package com.example.campaignservice.sevice;

import com.example.campaignservice.model.Campaign;
import com.example.campaignservice.model.CampaignStatus;
import com.example.campaignservice.repositoy.CampaignRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Data
@Service
public class CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    public Optional<Campaign> getCampaign(final Long id) {
        return campaignRepository.findById(id);
    }

    public Iterable<Campaign> getCampaigns() {
        return campaignRepository.findAll();
    }

    public Iterable<Campaign> getCampaignsByOrganizer(Long organizerId) {
        return campaignRepository.findByOrganizerId(organizerId);
    }

    public Iterable<Campaign> getCampaignsByStatus(CampaignStatus status) {
        return campaignRepository.findByStatus(status);
    }

    public void deleteCampaign(final Long id) {
        campaignRepository.findById(id).ifPresent(campaign -> {
            campaign.setStatus(CampaignStatus.ARCHIVE);
            campaign.setUpdatedAt(LocalDateTime.now());
            campaignRepository.save(campaign);
        });
    }

    public Campaign saveCampaign(Campaign campaign) {
        if (campaign.getId() == null) {
            campaign.setReference("CAMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            campaign.setCreatedAt(LocalDateTime.now());
        }
        campaign.setUpdatedAt(LocalDateTime.now());
        return campaignRepository.save(campaign);
    }

    public Campaign updateCampaignStatus(Long id, CampaignStatus status) {
        return campaignRepository.findById(id).map(campaign -> {
            campaign.setStatus(status);
            campaign.setUpdatedAt(LocalDateTime.now());
            return campaignRepository.save(campaign);
        }).orElse(null);
    }
}
