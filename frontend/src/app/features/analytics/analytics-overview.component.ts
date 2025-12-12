import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalyticsService } from '../../core/services/analytics.service';
import { CampaignMetrics, EventMetrics } from '../../core/models/analytics.model';

@Component({
  selector: 'app-analytics-overview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './analytics-overview.component.html',
  styleUrl: './analytics-overview.component.css',
})
export class AnalyticsOverviewComponent implements OnInit {
  private analyticsService = inject(AnalyticsService);

  campaignMetrics = signal<CampaignMetrics[]>([]);
  eventMetrics = signal<EventMetrics[]>([]);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);

  // Computed statistics
  totalCampaigns = computed(() => this.campaignMetrics().length);
  totalEvents = computed(() => this.eventMetrics().length);
  totalSent = computed(() => 
    this.campaignMetrics().reduce((sum, m) => sum + (m.emailsSent || 0), 0)
  );
  totalRegistrations = computed(() =>
    this.eventMetrics().reduce((sum, m) => sum + (m.totalRegistrations || 0), 0)
  );

  ngOnInit(): void {
    this.loadMetrics();
  }

  loadMetrics(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    // Load both campaign and event metrics
    this.analyticsService.getCampaignMetrics().subscribe({
      next: (metrics) => {
        this.campaignMetrics.set(metrics);
        this.checkLoadingComplete();
      },
      error: (error) => {
        this.errorMessage.set('Erreur lors du chargement des métriques de campagnes');
        this.checkLoadingComplete();
        console.error('Error loading campaign metrics:', error);
      }
    });

    this.analyticsService.getEventMetrics().subscribe({
      next: (metrics) => {
        this.eventMetrics.set(metrics);
        this.checkLoadingComplete();
      },
      error: (error) => {
        this.errorMessage.set('Erreur lors du chargement des métriques d\'événements');
        this.checkLoadingComplete();
        console.error('Error loading event metrics:', error);
      }
    });
  }

  private checkLoadingComplete(): void {
    // Simple check - in a real app, you'd use a more sophisticated loading state
    if (!this.isLoading()) return;
    setTimeout(() => this.isLoading.set(false), 500);
  }
}


