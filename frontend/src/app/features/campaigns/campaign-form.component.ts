// src/app/features/campaigns/campaign-form.component.ts
import { Component, Input, Output, EventEmitter, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CampaignService, Campaign, CampaignFormData, CampaignStatus, Channel } from '../../core/services/campaign.service';

@Component({
  selector: 'app-campaign-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './campaign-form.component.html',
  styleUrls: ['./campaign-form.component.css']
})
export class CampaignFormComponent implements OnInit {
  private campaignService = inject(CampaignService);

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
    organizerId: 1 // Default organizer ID - in real app, get from auth
  };

  channelOptions: { value: Channel, label: string }[] = [
    { value: 'EMAIL', label: 'Email' },
    { value: 'SMS', label: 'SMS' },
    { value: 'PUSH', label: 'Push Notification' },
    { value: 'MULTI_CANAL', label: 'Multi-channel' }
  ];

  newSegment = '';

  ngOnInit(): void {
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

    if (this.isEditing && this.campaign) {
      this.campaignService.updateCampaign(this.campaign.id, this.formData).subscribe({
        next: () => {
          alert('Campaign updated successfully!');
          this.submit.emit();
        },
        error: (error) => {
          console.error('Error updating campaign:', error);
          alert('Failed to update campaign. Please try again.');
        }
      });
    } else {
      this.campaignService.createCampaign(this.formData).subscribe({
        next: () => {
          alert('Campaign created successfully!');
          this.submit.emit();
        },
        error: (error) => {
          console.error('Error creating campaign:', error);
          alert('Failed to create campaign. Please try again.');
        }
      });
    }
  }

  onCancel(): void {
    this.cancel.emit();
  }
}
