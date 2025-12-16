import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CampaignMetrics, EventMetrics } from '../models/analytics.model';
import { getApiUrl } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class AnalyticsService {
  private http = inject(HttpClient);
  private readonly apiUrl = getApiUrl('/api/analytics');

  // Campaign Metrics
  getCampaignMetrics(): Observable<CampaignMetrics[]> {
    console.log('=== AnalyticsService: getCampaignMetrics ===');
    console.log('API URL:', `${this.apiUrl}/campaigns`);
    return this.http.get<CampaignMetrics[]>(`${this.apiUrl}/campaigns`);
  }

  getCampaignMetricsByCampaign(campaignId: number): Observable<CampaignMetrics[]> {
    console.log('=== AnalyticsService: getCampaignMetricsByCampaign ===');
    console.log('Campaign ID:', campaignId);
    return this.http.get<CampaignMetrics[]>(`${this.apiUrl}/campaigns/${campaignId}`);
  }

  createCampaignMetrics(metrics: CampaignMetrics): Observable<CampaignMetrics> {
    return this.http.post<CampaignMetrics>(`${this.apiUrl}/campaigns`, metrics);
  }

  // Event Metrics
  getEventMetrics(): Observable<EventMetrics[]> {
    console.log('=== AnalyticsService: getEventMetrics ===');
    console.log('API URL:', `${this.apiUrl}/events`);
    return this.http.get<EventMetrics[]>(`${this.apiUrl}/events`);
  }

  getEventMetricsByEvent(eventId: number): Observable<EventMetrics[]> {
    console.log('=== AnalyticsService: getEventMetricsByEvent ===');
    console.log('Event ID:', eventId);
    return this.http.get<EventMetrics[]>(`${this.apiUrl}/events/${eventId}`);
  }

  createEventMetrics(metrics: EventMetrics): Observable<EventMetrics> {
    return this.http.post<EventMetrics>(`${this.apiUrl}/events`, metrics);
  }

  // Calculate metrics endpoints
  calculateCampaignMetrics(): Observable<CampaignMetrics[]> {
    console.log('=== AnalyticsService: calculateCampaignMetrics ===');
    return this.http.post<CampaignMetrics[]>(`${this.apiUrl}/campaigns/calculate`, {});
  }

  calculateEventMetrics(): Observable<EventMetrics[]> {
    console.log('=== AnalyticsService: calculateEventMetrics ===');
    return this.http.post<EventMetrics[]>(`${this.apiUrl}/events/calculate`, {});
  }

  calculateAllMetrics(): Observable<{ campaignMetrics: CampaignMetrics[], eventMetrics: EventMetrics[], message: string }> {
    console.log('=== AnalyticsService: calculateAllMetrics ===');
    return this.http.post<{ campaignMetrics: CampaignMetrics[], eventMetrics: EventMetrics[], message: string }>(
      `${this.apiUrl}/calculate-all`, 
      {}
    );
  }
}
