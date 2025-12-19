// src/app/features/campaigns/campaign-form.component.ts
import { Component, Input, Output, EventEmitter, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CampaignService, Campaign, CampaignFormData, CampaignStatus, Channel } from '../../core/services/campaign.service';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-campaign-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './campaign-form.component.html',
  styleUrls: ['./campaign-form.component.css']
})
export class CampaignFormComponent implements OnInit {
  private campaignService = inject(CampaignService);
  private authService = inject(AuthService);

  @Input() campaign: Campaign | null = null;
  @Output() cancel = new EventEmitter<void>();
  @Output() submit = new EventEmitter<void>();

  isEditing = false;
  formData: CampaignFormData = {
    name: '',
    description: '',
    startDate: '',
    endDate: '',
    budget: 0,
    channel: 'EMAIL',
    targetSegments: [],
    organizerId: 0 // Sera défini depuis l'utilisateur connecté
  };

  channelOptions: { value: Channel, label: string }[] = [
    { value: 'EMAIL', label: 'Email' },
    { value: 'SMS', label: 'SMS' },
    { value: 'PUSH', label: 'Push Notification' },
    { value: 'MULTI_CANAL', label: 'Multi-channel' }
  ];

  newSegment = '';

  ngOnInit(): void {
    // Définir l'organizerId depuis l'utilisateur connecté
    const currentUser = this.authService.currentUser();
    if (currentUser && currentUser.id) {
      this.formData.organizerId = currentUser.id;
      console.log('Organizer ID défini depuis l\'utilisateur connecté:', this.formData.organizerId);
    } else {
      console.warn('⚠ Aucun utilisateur connecté trouvé, organizerId sera 0');
    }
    
    if (this.campaign) {
      this.isEditing = true;
      this.formData = {
        name: this.campaign.name,
        description: this.campaign.description || '',
        startDate: this.campaign.startDate.split('T')[0],
        endDate: this.campaign.endDate.split('T')[0],
        budget: this.campaign.budget || 0,
        channel: this.campaign.channel,
        targetSegments: [...this.campaign.targetSegments],
        organizerId: this.campaign.organizerId
      };
    }
  }

  addSegment(): void {
    if (this.newSegment.trim() && !this.formData.targetSegments.includes(this.newSegment.trim())) {
      this.formData.targetSegments.push(this.newSegment.trim());
      this.newSegment = '';
    }
  }

  removeSegment(segment: string): void {
    this.formData.targetSegments = this.formData.targetSegments.filter(s => s !== segment);
  }

  onSubmit(): void {
    // Validate dates
    const startDate = new Date(this.formData.startDate);
    const endDate = new Date(this.formData.endDate);

    if (endDate <= startDate) {
      alert('End date must be after start date');
      return;
    }

    // Validation de l'organizerId
    if (!this.formData.organizerId || this.formData.organizerId === 0) {
      const currentUser = this.authService.currentUser();
      if (currentUser && currentUser.id) {
        this.formData.organizerId = currentUser.id;
      } else {
        alert('Erreur: Vous devez être connecté pour créer une campagne');
        return;
      }
    }
    
    // Convert dates to ISO format for backend (LocalDateTime)
    // Format: yyyy-MM-ddTHH:mm:ss
    const startDateStr = this.formData.startDate + 'T00:00:00';
    const endDateStr = this.formData.endDate + 'T23:59:59';
    
    const campaignData = {
      name: this.formData.name.trim(),
      description: this.formData.description?.trim() || '',
      startDate: startDateStr,
      endDate: endDateStr,
      budget: this.formData.budget || 0,
      channel: this.formData.channel,
      targetSegments: this.formData.targetSegments || [],
      organizerId: this.formData.organizerId,
      status: this.isEditing && this.campaign ? this.campaign.status : 'BROUILLON' // Default status for new campaigns
    };
    
    console.log('Dates formatées:');
    console.log('  startDate:', startDateStr);
    console.log('  endDate:', endDateStr);

    console.log('=== CampaignForm: Submitting campaign ===');
    console.log('Is editing:', this.isEditing);
    console.log('Campaign data:', campaignData);

    if (this.isEditing && this.campaign) {
      this.campaignService.updateCampaign(this.campaign.id, campaignData).subscribe({
        next: (updatedCampaign) => {
          console.log('Campaign updated successfully:', updatedCampaign);
          alert('Campaign updated successfully!');
          this.submit.emit();
        },
        error: (error) => {
          console.error('=== CampaignForm: Error updating campaign ===');
          console.error('Error details:', {
            status: error.status,
            statusText: error.statusText,
            message: error.message,
            error: error.error
          });
          const errorMsg = error.error?.message || error.message || 'Failed to update campaign';
          alert(`Failed to update campaign: ${errorMsg}`);
        }
      });
    } else {
      this.campaignService.createCampaign(campaignData).subscribe({
        next: (createdCampaign) => {
          console.log('Campaign created successfully:', createdCampaign);
          alert('Campaign created successfully!');
          this.submit.emit();
        },
        error: (error) => {
          console.error('=== CampaignForm: Error creating campaign ===');
          console.error('Error details:', {
            status: error.status,
            statusText: error.statusText,
            message: error.message,
            error: error.error
          });
          
          let errorMsg = 'Failed to create campaign';
          if (error.error) {
            if (error.error.message) {
              errorMsg = error.error.message;
            } else if (typeof error.error === 'string') {
              errorMsg = error.error;
            } else if (error.error.error) {
              errorMsg = error.error.error + (error.error.message ? ': ' + error.error.message : '');
            }
          } else if (error.message) {
            errorMsg = error.message;
          }
          
          alert(`Failed to create campaign: ${errorMsg}`);
        }
      });
    }
  }

  onCancel(): void {
    this.cancel.emit();
  }
}
