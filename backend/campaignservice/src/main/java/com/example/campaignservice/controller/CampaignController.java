package com.example.campaignservice.controller;

import com.example.campaignservice.dto.CampaignCreateRequest;
import com.example.campaignservice.model.Campaign;
import com.example.campaignservice.model.CampaignStatus;
import com.example.campaignservice.model.Channel;
import com.example.campaignservice.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<?> createCampaign(@RequestBody CampaignCreateRequest request) {
        System.out.println("=== CampaignController.createCampaign() appelé ===");
        System.out.println("CampaignCreateRequest reçue:");
        System.out.println("  - Name: " + request.getName());
        System.out.println("  - Description: " + request.getDescription());
        System.out.println("  - StartDate: " + request.getStartDate());
        System.out.println("  - EndDate: " + request.getEndDate());
        System.out.println("  - Budget: " + request.getBudget());
        System.out.println("  - Status: " + request.getStatus());
        System.out.println("  - Channel: " + request.getChannel());
        System.out.println("  - OrganizerId: " + request.getOrganizerId());
        System.out.println("  - TargetSegments: " + request.getTargetSegments());
        
        try {
            // Convertir le DTO en entité Campaign
            Campaign campaign = new Campaign();
            campaign.setName(request.getName());
            campaign.setDescription(request.getDescription());
            
            // Parser les dates depuis les strings
            LocalDateTime startDate = request.getStartDateAsLocalDateTime();
            LocalDateTime endDate = request.getEndDateAsLocalDateTime();
            
            System.out.println("  - StartDate parsée: " + startDate);
            System.out.println("  - EndDate parsée: " + endDate);
            
            campaign.setStartDate(startDate);
            campaign.setEndDate(endDate);
            campaign.setBudget(request.getBudget());
            campaign.setStatus(request.getStatus() != null ? request.getStatus() : CampaignStatus.BROUILLON);
            campaign.setChannel(request.getChannel());
            campaign.setOrganizerId(request.getOrganizerId());
            campaign.setTargetSegments(request.getTargetSegments() != null ? request.getTargetSegments() : new ArrayList<>());
            
            Campaign saved = campaignService.saveCampaign(campaign);
            System.out.println("✅ Campaign créée avec succès - ID: " + saved.getId() + ", Reference: " + saved.getReference());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Erreur de validation: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validation error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création de la campagne:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "N/A"));
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCampaign(@PathVariable("id") final Long id, @RequestBody CampaignCreateRequest request) {
        System.out.println("=== CampaignController.updateCampaign() appelé ===");
        System.out.println("Campaign ID: " + id);
        System.out.println("CampaignCreateRequest reçue:");
        System.out.println("  - Name: " + request.getName());
        System.out.println("  - Description: " + request.getDescription());
        System.out.println("  - StartDate: " + request.getStartDate());
        System.out.println("  - EndDate: " + request.getEndDate());
        System.out.println("  - Budget: " + request.getBudget());
        System.out.println("  - Status: " + request.getStatus());
        System.out.println("  - Channel: " + request.getChannel());
        System.out.println("  - OrganizerId: " + request.getOrganizerId());
        System.out.println("  - TargetSegments: " + request.getTargetSegments());
        
        return campaignService.getCampaign(id).map(existing -> {
            System.out.println("Campaign existante trouvée - ID: " + existing.getId() + ", Reference: " + existing.getReference());
            
            existing.setName(request.getName());
            existing.setDescription(request.getDescription());
            
            // Parser les dates depuis les strings
            if (request.getStartDate() != null) {
                LocalDateTime startDate = request.getStartDateAsLocalDateTime();
                System.out.println("  StartDate parsée: " + startDate);
                existing.setStartDate(startDate);
            }
            if (request.getEndDate() != null) {
                LocalDateTime endDate = request.getEndDateAsLocalDateTime();
                System.out.println("  EndDate parsée: " + endDate);
                existing.setEndDate(endDate);
            }
            
            existing.setBudget(request.getBudget());
            if (request.getStatus() != null) {
                existing.setStatus(request.getStatus());
            }
            existing.setChannel(request.getChannel());
            existing.setTargetSegments(request.getTargetSegments() != null ? request.getTargetSegments() : new ArrayList<>());
            
            try {
                Campaign saved = campaignService.saveCampaign(existing);
                System.out.println("✅ Campaign mise à jour avec succès - ID: " + saved.getId() + ", Reference: " + saved.getReference());
                return ResponseEntity.ok(saved);
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la mise à jour de la campagne:");
                System.err.println("   Message: " + e.getMessage());
                e.printStackTrace();
                Map<String, String> error = new HashMap<>();
                error.put("error", "Internal server error");
                error.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status/{status}")
    public Campaign updateCampaignStatus(@PathVariable("id") final Long id, @PathVariable("status") final CampaignStatus status) {
        return campaignService.updateCampaignStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCampaign(@PathVariable("id") final Long id) {
        System.out.println("=== CampaignController.deleteCampaign() appelé ===");
        System.out.println("Campaign ID à supprimer: " + id);
        
        try {
            campaignService.deleteCampaign(id);
            System.out.println("✅ Campaign supprimée définitivement avec succès - ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Erreur de validation: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Not found");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression de la campagne:");
            System.err.println("   Message: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

}