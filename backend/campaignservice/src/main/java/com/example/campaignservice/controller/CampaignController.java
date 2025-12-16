package com.example.campaignservice.controller;

import com.example.campaignservice.model.Campaign;
import com.example.campaignservice.model.CampaignStatus;
import com.example.campaignservice.model.Channel;
import com.example.campaignservice.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/campaigns")
// CORS is handled by the Gateway (CorsConfig), no need for @CrossOrigin here
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @GetMapping("/")
    public String home() {
        return "Campaign Service is running!";
    }

    @GetMapping
    public List<Campaign> getCampaigns() {
        Iterable<Campaign> campaigns = campaignService.getCampaigns();
        return StreamSupport.stream(campaigns.spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/organizer/{organizerId}")
    public List<Campaign> getCampaignsByOrganizer(@PathVariable("organizerId") final Long organizerId) {
        Iterable<Campaign> campaigns = campaignService.getCampaignsByOrganizer(organizerId);
        return StreamSupport.stream(campaigns.spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/status/{status}")
    public List<Campaign> getCampaignsByStatus(@PathVariable("status") final CampaignStatus status) {
        Iterable<Campaign> campaigns = campaignService.getCampaignsByStatus(status);
        return StreamSupport.stream(campaigns.spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Campaign getCampaign(@PathVariable("id") final Long id) {
        return campaignService.getCampaign(id).orElse(null);
    }

    @PostMapping
    public Campaign createCampaign(@RequestBody Campaign campaign) {
        return campaignService.saveCampaign(campaign);
    }

    @PutMapping("/{id}")
    public Campaign updateCampaign(@PathVariable("id") final Long id, @RequestBody Campaign campaign) {
        return campaignService.getCampaign(id).map(existing -> {
            existing.setName(campaign.getName());
            existing.setDescription(campaign.getDescription());
            existing.setStartDate(campaign.getStartDate());
            existing.setEndDate(campaign.getEndDate());
            existing.setBudget(campaign.getBudget());
            existing.setStatus(campaign.getStatus());
            existing.setChannel(campaign.getChannel());
            existing.setTargetSegments(campaign.getTargetSegments());
            return campaignService.saveCampaign(existing);
        }).orElse(null);
    }

    @PatchMapping("/{id}/status/{status}")
    public Campaign updateCampaignStatus(@PathVariable("id") final Long id, @PathVariable("status") final CampaignStatus status) {
        return campaignService.updateCampaignStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void deleteCampaign(@PathVariable("id") final Long id) {
        campaignService.deleteCampaign(id);
    }

}