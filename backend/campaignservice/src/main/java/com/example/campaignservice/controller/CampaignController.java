package com.example.campaignservice.controller;

import com.example.campaignservice.model.Campaign;
import com.example.campaignservice.model.CampaignStatus;
import com.example.campaignservice.model.Channel;
import com.example.campaignservice.sevice.CampaignService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @GetMapping("/")
    public String home() {
        return "Campaign Service is running!";
    }

    @Retry(name = "campaignRetry", fallbackMethod = "fallbackCampaignsCB")
    @CircuitBreaker(name = "campaignCB", fallbackMethod = "fallbackCampaignsCB")
    @GetMapping
    public Iterable<Campaign> getCampaigns() {
        simulateRandomFailure();
        return campaignService.getCampaigns();
    }

    @GetMapping("/organizer/{organizerId}")
    public Iterable<Campaign> getCampaignsByOrganizer(@PathVariable("organizerId") final Long organizerId) {
        return campaignService.getCampaignsByOrganizer(organizerId);
    }

    @GetMapping("/status/{status}")
    public Iterable<Campaign> getCampaignsByStatus(@PathVariable("status") final CampaignStatus status) {
        return campaignService.getCampaignsByStatus(status);
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

    private void simulateRandomFailure() {
        if (Math.random() < 0.3) {
            throw new RuntimeException("Simulated random failure in Campaign Service");
        }
    }

    public Iterable<Campaign> fallbackCampaignsCB(Exception e) {
        System.err.println("Campaign Service Fallback: " + e.getMessage());
        return List.of(
                new Campaign(1L, "CAMP-FALLBACK", "Fallback Campaign", "Fallback campaign for circuit breaker",
                        LocalDateTime.now(), LocalDateTime.now().plusDays(7), 1000.0,
                        CampaignStatus.BROUILLON, Channel.EMAIL, 1L, List.of("fallback-segment"), null, null)
        );
    }
}