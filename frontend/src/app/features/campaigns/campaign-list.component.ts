// src/app/features/campaigns/campaign-list.component.ts
import { Component, OnInit, OnDestroy, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { CampaignService, Campaign, CampaignStatus, Channel } from '../../core/services/campaign.service';
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
  errorMessage = signal<string | null>(null);

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
    this.errorMessage.set(null);
    console.log('Loading campaigns from:', this.campaignService);
    this.campaignService.getAllCampaigns().subscribe({
      next: (data) => {
        console.log('Campaigns received:', data);
        console.log('Campaigns type:', typeof data, Array.isArray(data));
        console.log('Campaigns count:', Array.isArray(data) ? data.length : 0);
        // Ensure data is an array
        const campaignsArray = Array.isArray(data) ? data : [];
        console.log('Setting campaigns array with', campaignsArray.length, 'items');
        this.campaigns.set(campaignsArray);
        this.filterCampaigns();
        this.isLoading.set(false);
        this.errorMessage.set(null);
      },
      error: (error) => {
        console.error('Error loading campaigns:', error);
        console.error('Error details:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          error: error.error
        });
        this.campaigns.set([]);
        this.filterCampaigns();
        this.isLoading.set(false);
        const errorMsg = error.status 
          ? `Erreur ${error.status}: ${error.statusText || error.message}`
          : `Erreur lors du chargement: ${error.message || 'Erreur inconnue'}`;
        this.errorMessage.set(errorMsg);
      }
    });
  }

  filterCampaigns(): void {
    let filtered = this.campaigns();
    console.log('Filtering campaigns. Total:', filtered.length, 'Status filter:', this.selectedStatus, 'Search:', this.searchQuery);

    // Filter by status
    if (this.selectedStatus !== 'ALL') {
      filtered = filtered.filter(c => c.status === this.selectedStatus);
      console.log('After status filter:', filtered.length);
    }
    // Note: Plus besoin d'exclure ARCHIVE car les campagnes sont maintenant supprimées définitivement

    // Filter by search query
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(c =>
        c.name.toLowerCase().includes(query) ||
        (c.description && c.description.toLowerCase().includes(query)) ||
        c.reference.toLowerCase().includes(query)
      );
      console.log('After search filter:', filtered.length);
    }

    console.log('Final filtered campaigns:', filtered.length);
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
    if (confirm(`⚠ ATTENTION : Êtes-vous sûr de vouloir SUPPRIMER DÉFINITIVEMENT la campagne "${campaign.name}" ?\n\nCette action est irréversible. La campagne sera supprimée de la base de données.`)) {
      console.log('=== Suppression définitive de campagne ===');
      console.log('Campaign ID:', campaign.id);
      console.log('Campaign Name:', campaign.name);
      
      this.campaignService.deleteCampaign(campaign.id).subscribe({
        next: () => {
          console.log('Campagne supprimée définitivement avec succès');
          // Recharger la liste pour mettre à jour l'affichage
          this.loadCampaigns();
        },
        error: (error) => {
          console.error('=== Erreur lors de la suppression de la campagne ===');
          console.error('Error details:', {
            status: error.status,
            statusText: error.statusText,
            message: error.message,
            error: error.error
          });
          
          let errorMsg = 'Échec de la suppression de la campagne';
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
          
          alert(`Erreur: ${errorMsg}`);
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

  formatDate(dateString: string | null | undefined): string {
    if (!dateString) return 'N/A';
    try {
      const date = new Date(dateString);
      if (isNaN(date.getTime())) return 'Invalid date';
      return date.toLocaleDateString('fr-FR', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    } catch (error) {
      console.error('Error formatting date:', dateString, error);
      return 'Invalid date';
    }
  }

  formatCurrency(amount: number | null | undefined): string {
    if (amount === null || amount === undefined) return 'N/A';
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount || 0);
  }
}
