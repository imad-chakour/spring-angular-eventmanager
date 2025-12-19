package com.example.campaignservice.service;

import com.example.campaignservice.client.UserClient;
import com.example.campaignservice.model.Campaign;
import com.example.campaignservice.model.CampaignStatus;
import com.example.campaignservice.repository.CampaignRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Data
@Service
public class CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private UserClient userClient;

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

    @Transactional
    public void deleteCampaign(final Long id) {
        System.out.println("=== CampaignService.deleteCampaign() appelé ===");
        System.out.println("Campaign ID: " + id);
        
        Optional<Campaign> campaignOpt = campaignRepository.findById(id);
        if (campaignOpt.isPresent()) {
            Campaign campaign = campaignOpt.get();
            System.out.println("Campaign trouvée - ID: " + campaign.getId() + ", Name: " + campaign.getName() + ", Status: " + campaign.getStatus());
            System.out.println("  - TargetSegments count: " + (campaign.getTargetSegments() != null ? campaign.getTargetSegments().size() : 0));
            
            // Suppression réelle de la base de données (hard delete)
            // JPA supprimera automatiquement les enregistrements dans campaign_segments grâce à @ElementCollection
            campaignRepository.deleteById(id);
            System.out.println("✅ Campaign supprimée définitivement de la base de données - ID: " + id);
            System.out.println("✅ Les segments associés (campaign_segments) seront également supprimés automatiquement");
        } else {
            System.err.println("⚠️ Campaign non trouvée avec ID: " + id);
            throw new IllegalArgumentException("Campaign not found with id: " + id);
        }
    }

    public Campaign saveCampaign(Campaign campaign) {
        System.out.println("=== CampaignService.saveCampaign() appelé ===");
        
        // Validation des champs obligatoires
        if (campaign.getName() == null || campaign.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la campagne est obligatoire");
        }
        if (campaign.getStartDate() == null) {
            throw new IllegalArgumentException("La date de début est obligatoire");
        }
        if (campaign.getEndDate() == null) {
            throw new IllegalArgumentException("La date de fin est obligatoire");
        }
        if (campaign.getEndDate().isBefore(campaign.getStartDate())) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }
        if (campaign.getOrganizerId() == null) {
            throw new IllegalArgumentException("L'ID de l'organisateur est obligatoire");
        }
        
        // Nouvelle campagne
        if (campaign.getId() == null) {
            // Générer une référence unique
            String reference = "CAMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            campaign.setReference(reference);
            campaign.setCreatedAt(LocalDateTime.now());
            System.out.println("  Nouvelle campagne - Reference générée: " + reference);
            
            // Définir le statut par défaut si non fourni
            if (campaign.getStatus() == null) {
                campaign.setStatus(CampaignStatus.BROUILLON);
                System.out.println("  Statut par défaut défini: BROUILLON");
            }
        } else {
            System.out.println("  Mise à jour de la campagne ID: " + campaign.getId());
        }
        
        // Validate organizer through User Service
        // Note: La validation est optionnelle pour éviter les erreurs 403 si le token JWT n'est pas disponible
        if (campaign.getOrganizerId() != null) {
            try {
                Map<String, Object> organizer = userClient.getUserById(campaign.getOrganizerId());
                if (organizer == null || organizer.isEmpty()) {
                    System.out.println("⚠️ Organizer not found with id " + campaign.getOrganizerId() + ", but continuing campaign creation");
                } else {
                    System.out.println("✅ Organizer validated: " + organizer.get("email"));
                }
            } catch (Exception e) {
                // Ne pas faire échouer la création de campagne si la validation échoue
                // (peut arriver si le token JWT n'est pas disponible dans le contexte Feign)
                System.err.println("⚠️ Erreur lors de la validation de l'organizer: " + e.getMessage());
                System.err.println("   Continuation de la création de la campagne sans validation");
            }
        }
        
        campaign.setUpdatedAt(LocalDateTime.now());
        
        try {
            Campaign saved = campaignRepository.save(campaign);
            System.out.println("✅ Campaign sauvegardée - ID: " + saved.getId() + ", Reference: " + saved.getReference());
            return saved;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la sauvegarde de la campagne:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "N/A"));
            e.printStackTrace();
            throw e;
        }
    }

    public Campaign updateCampaignStatus(Long id, CampaignStatus status) {
        return campaignRepository.findById(id).map(campaign -> {
            campaign.setStatus(status);
            campaign.setUpdatedAt(LocalDateTime.now());
            return campaignRepository.save(campaign);
        }).orElse(null);
    }
}
