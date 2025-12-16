// src/app/features/campaigns/campaign-status-badge.component.ts
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CampaignStatus } from '../../core/services/campaign.service';

@Component({
  selector: 'app-campaign-status-badge',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span [class]="getStatusClass()">
      {{ getStatusLabel() }}
    </span>
  `,
  styles: [`
    .status-badge {
      display: inline-flex;
      align-items: center;
      padding: 0.25rem 0.75rem;
      border-radius: 9999px;
      font-size: 0.75rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }

    .status-draft {
      background-color: #f3f4f6;
      color: #374151;
    }

    .status-active {
      background-color: #d1fae5;
      color: #065f46;
    }

    .status-completed {
      background-color: #dbeafe;
      color: #1e40af;
    }

    .status-archived {
      background-color: #f3f4f6;
      color: #6b7280;
    }
  `]
})
export class CampaignStatusBadgeComponent {
  @Input() status!: CampaignStatus;

  getStatusClass(): string {
    const baseClass = 'status-badge';
    switch (this.status) {
      case 'BROUILLON': return `${baseClass} status-draft`;
      case 'ACTIF': return `${baseClass} status-active`;
      case 'TERMINE': return `${baseClass} status-completed`;
      case 'ARCHIVE': return `${baseClass} status-archived`;
      default: return `${baseClass} status-draft`;
    }
  }

  getStatusLabel(): string {
    switch (this.status) {
      case 'BROUILLON': return 'Draft';
      case 'ACTIF': return 'Active';
      case 'TERMINE': return 'Completed';
      case 'ARCHIVE': return 'Archived';
      default: return this.status;
    }
  }
}
