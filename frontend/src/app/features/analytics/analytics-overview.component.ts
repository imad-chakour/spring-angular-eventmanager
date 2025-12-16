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
    console.log('=== AnalyticsOverviewComponent: Loading metrics ===');

    // Load both campaign and event metrics
    this.analyticsService.getCampaignMetrics().subscribe({
      next: (metrics) => {
        console.log('Campaign metrics received:', metrics);
        console.log('Campaign metrics type:', typeof metrics, Array.isArray(metrics));
        console.log('Campaign metrics count:', Array.isArray(metrics) ? metrics.length : 0);
        const metricsArray = Array.isArray(metrics) ? metrics : [];
        this.campaignMetrics.set(metricsArray);
        this.checkLoadingComplete();
      },
      error: (error) => {
        console.error('=== AnalyticsOverviewComponent: Error loading campaign metrics ===');
        console.error('Error details:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          error: error.error
        });
        this.errorMessage.set('Erreur lors du chargement des métriques de campagnes');
        this.campaignMetrics.set([]);
        this.checkLoadingComplete();
      }
    });

    this.analyticsService.getEventMetrics().subscribe({
      next: (metrics) => {
        console.log('Event metrics received:', metrics);
        console.log('Event metrics type:', typeof metrics, Array.isArray(metrics));
        console.log('Event metrics count:', Array.isArray(metrics) ? metrics.length : 0);
        const metricsArray = Array.isArray(metrics) ? metrics : [];
        this.eventMetrics.set(metricsArray);
        this.checkLoadingComplete();
      },
      error: (error) => {
        console.error('=== AnalyticsOverviewComponent: Error loading event metrics ===');
        console.error('Error details:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          error: error.error
        });
        this.errorMessage.set('Erreur lors du chargement des métriques d\'événements');
        this.eventMetrics.set([]);
        this.checkLoadingComplete();
      }
    });
  }

  private checkLoadingComplete(): void {
    // Simple check - in a real app, you'd use a more sophisticated loading state
    if (!this.isLoading()) return;
    setTimeout(() => this.isLoading.set(false), 500);
  }

  calculateAllMetrics(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    console.log('=== AnalyticsOverviewComponent: Calculating all metrics ===');

    this.analyticsService.calculateAllMetrics().subscribe({
      next: (result) => {
        console.log('Metrics calculated:', result);
        // Recharger les métriques après calcul
        this.loadMetrics();
      },
      error: (error) => {
        console.error('=== AnalyticsOverviewComponent: Error calculating metrics ===');
        console.error('Error details:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          error: error.error
        });
        this.errorMessage.set('Erreur lors du calcul des métriques');
        this.isLoading.set(false);
      }
    });
  }

  calculateCampaignMetrics(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    console.log('=== AnalyticsOverviewComponent: Calculating campaign metrics ===');

    this.analyticsService.calculateCampaignMetrics().subscribe({
      next: (metrics) => {
        console.log('Campaign metrics calculated:', metrics);
        this.loadMetrics();
      },
      error: (error) => {
        console.error('Error calculating campaign metrics:', error);
        this.errorMessage.set('Erreur lors du calcul des métriques de campagnes');
        this.isLoading.set(false);
      }
    });
  }

  calculateEventMetrics(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    console.log('=== AnalyticsOverviewComponent: Calculating event metrics ===');

    this.analyticsService.calculateEventMetrics().subscribe({
      next: (metrics) => {
        console.log('Event metrics calculated:', metrics);
        this.loadMetrics();
      },
      error: (error) => {
        console.error('Error calculating event metrics:', error);
        this.errorMessage.set('Erreur lors du calcul des métriques d\'événements');
        this.isLoading.set(false);
      }
    });
  }
}


