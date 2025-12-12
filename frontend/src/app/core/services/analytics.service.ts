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
    return this.http.get<CampaignMetrics[]>(`${this.apiUrl}/campaigns`);
  }

  getCampaignMetricsByCampaign(campaignId: number): Observable<CampaignMetrics[]> {
    return this.http.get<CampaignMetrics[]>(`${this.apiUrl}/campaigns/${campaignId}`);
  }

  createCampaignMetrics(metrics: CampaignMetrics): Observable<CampaignMetrics> {
    return this.http.post<CampaignMetrics>(`${this.apiUrl}/campaigns`, metrics);
  }

  // Event Metrics
  getEventMetrics(): Observable<EventMetrics[]> {
    return this.http.get<EventMetrics[]>(`${this.apiUrl}/events`);
  }

  getEventMetricsByEvent(eventId: number): Observable<EventMetrics[]> {
    return this.http.get<EventMetrics[]>(`${this.apiUrl}/events/${eventId}`);
  }

  createEventMetrics(metrics: EventMetrics): Observable<EventMetrics> {
    return this.http.post<EventMetrics>(`${this.apiUrl}/events`, metrics);
  }
}
