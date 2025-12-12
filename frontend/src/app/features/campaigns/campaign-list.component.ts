// src/app/features/campaigns/campaign-list.component.ts
import { Component, OnInit, OnDestroy, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { CampaignService, Campaign, CampaignStatus, Channel } from './campaign.service';
import { CampaignFormComponent } from './campaign-form.component';
import { CampaignStatusBadgeComponent } from './campaign-status-badge.component';
import { filter, Subscription } from 'rxjs';

@Component({
  selector: 'app-campaign-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, CampaignFormComponent, CampaignStatusBadgeComponent],
  templateUrl: './campaign-list.component.html',
  styleUrls: ['./campaign-list.component.css']
})
export class CampaignListComponent implements OnInit, OnDestroy {
  private campaignService = inject(CampaignService);
  private router = inject(Router);
  private routerSubscription?: Subscription;

  campaigns = signal<Campaign[]>([]);
  filteredCampaigns = signal<Campaign[]>([]);
  selectedStatus: CampaignStatus | 'ALL' = 'ALL';
  searchQuery = '';
  isCreating = false;
  selectedCampaign: Campaign | null = null;
  isLoading = signal(true);

  // Computed properties for statistics
  totalCampaigns = computed(() => this.campaigns().length);
  activeCampaigns = computed(() => this.campaigns().filter(c => c.status === 'ACTIF').length);
  draftCampaigns = computed(() => this.campaigns().filter(c => c.status === 'BROUILLON').length);
  completedCampaigns = computed(() => this.campaigns().filter(c => c.status === 'TERMINE').length);
  archivedCampaigns = computed(() => this.campaigns().filter(c => c.status === 'ARCHIVE').length);

  // Status options for filter
  statusOptions: { value: CampaignStatus | 'ALL', label: string }[] = [
    { value: 'ALL', label: 'All Campaigns' },
    { value: 'BROUILLON', label: 'Draft' },
    { value: 'ACTIF', label: 'Active' },
    { value: 'TERMINE', label: 'Completed' },
    { value: 'ARCHIVE', label: 'Archived' }
  ];

  // Channel options for display
  channelLabels: Record<Channel, string> = {
    EMAIL: 'Email',
    SMS: 'SMS',
    PUSH: 'Push Notification',
    MULTI_CANAL: 'Multi-channel'
  };

  ngOnInit(): void {
    this.loadCampaigns();
    // Rafraîchir automatiquement après redirection
    this.routerSubscription = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        if (this.router.url === '/campaigns') {
          this.loadCampaigns();
        }
      });
  }

  ngOnDestroy(): void {
    this.routerSubscription?.unsubscribe();
  }

  loadCampaigns(): void {
    this.isLoading.set(true);
    this.campaignService.getAllCampaigns().subscribe({
      next: (data) => {
        this.campaigns.set(data);
        this.filterCampaigns();
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading campaigns:', error);
        this.isLoading.set(false);
      }
    });
  }

  filterCampaigns(): void {
    let filtered = this.campaigns();

    // Filter by status
    if (this.selectedStatus !== 'ALL') {
      filtered = filtered.filter(c => c.status === this.selectedStatus);
    }

    // Filter by search query
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(c =>
        c.name.toLowerCase().includes(query) ||
        (c.description && c.description.toLowerCase().includes(query)) ||
        c.reference.toLowerCase().includes(query)
      );
    }

    this.filteredCampaigns.set(filtered);
  }

  onStatusFilterChange(): void {
    this.filterCampaigns();
  }

  onSearch(): void {
    this.filterCampaigns();
  }

  startCreate(): void {
    this.isCreating = true;
    this.selectedCampaign = null;
  }

  editCampaign(campaign: Campaign): void {
    this.selectedCampaign = campaign;
    this.isCreating = false;
  }

  deleteCampaign(campaign: Campaign): void {
    if (confirm(`Are you sure you want to delete campaign "${campaign.name}"?`)) {
      this.campaignService.deleteCampaign(campaign.id).subscribe({
        next: () => {
          this.loadCampaigns();
        },
        error: (error) => {
          console.error('Error deleting campaign:', error);
          alert('Failed to delete campaign. Please try again.');
        }
      });
    }
  }

  updateCampaignStatus(campaign: Campaign, status: CampaignStatus): void {
    this.campaignService.updateCampaignStatus(campaign.id, status).subscribe({
      next: () => {
        this.loadCampaigns();
      },
      error: (error) => {
        console.error('Error updating campaign status:', error);
        alert('Failed to update campaign status.');
      }
    });
  }

  onFormCancel(): void {
    this.isCreating = false;
    this.selectedCampaign = null;
  }

  onFormSubmit(): void {
    this.isCreating = false;
    this.selectedCampaign = null;
    this.loadCampaigns();
  }

  getChannelLabel(channel: Channel): string {
    return this.channelLabels[channel] || channel;
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    return new Date(dateString).toLocaleDateString();
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount || 0);
  }
}
