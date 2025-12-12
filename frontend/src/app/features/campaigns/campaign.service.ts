// src/app/core/services/campaign.service.ts
import { Injectable, inject, PLATFORM_ID, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { getApiUrl } from '../../core/config/api.config';

export interface Campaign {
  id: number;
  reference: string;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  budget: number;
  status: CampaignStatus;
  channel: Channel;
  organizerId: number;
  targetSegments: string[];
  createdAt: string;
  updatedAt: string;
}

export type CampaignStatus = 'BROUILLON' | 'ACTIF' | 'TERMINE' | 'ARCHIVE';
export type Channel = 'EMAIL' | 'SMS' | 'PUSH' | 'MULTI_CANAL';

export interface CampaignFormData {
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  budget: number;
  channel: Channel;
  targetSegments: string[];
  organizerId: number;
}

@Injectable({
  providedIn: 'root'
})
export class CampaignService {
  private http = inject(HttpClient);
  private platformId = inject(PLATFORM_ID);
  private apiUrl = getApiUrl('/api/campaigns'); // Via Spring Cloud Gateway

  // Mock data for SSR/prerendering
  private mockCampaigns: Campaign[] = [
    {
      id: 1,
      reference: 'CAMP-001',
      name: 'Summer Sale Campaign',
      description: 'Promotional campaign for summer products',
      startDate: '2024-06-01T00:00:00',
      endDate: '2024-08-31T23:59:59',
      budget: 5000,
      status: 'ACTIF',
      channel: 'EMAIL',
      organizerId: 1,
      targetSegments: ['VIP Customers', 'New Subscribers'],
      createdAt: '2024-05-15T10:30:00',
      updatedAt: '2024-05-15T10:30:00'
    },
    {
      id: 2,
      reference: 'CAMP-002',
      name: 'Product Launch',
      description: 'Launch campaign for new product line',
      startDate: '2024-07-01T00:00:00',
      endDate: '2024-07-31T23:59:59',
      budget: 10000,
      status: 'BROUILLON',
      channel: 'MULTI_CANAL',
      organizerId: 1,
      targetSegments: ['All Users'],
      createdAt: '2024-06-10T14:20:00',
      updatedAt: '2024-06-10T14:20:00'
    }
  ];

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  getAllCampaigns(): Observable<Campaign[]> {
    // Return mock data during SSR/prerendering
    if (!this.isBrowser()) {
      return of(this.mockCampaigns);
    }

    return this.http.get<Campaign[]>(this.apiUrl);
  }

  getCampaignById(id: number): Observable<Campaign> {
    if (!this.isBrowser()) {
      const campaign = this.mockCampaigns.find(c => c.id === id);
      return campaign ? of(campaign) : of(this.mockCampaigns[0]);
    }

    return this.http.get<Campaign>(`${this.apiUrl}/${id}`);
  }

  createCampaign(campaign: CampaignFormData): Observable<Campaign> {
    return this.http.post<Campaign>(this.apiUrl, campaign);
  }

  updateCampaign(id: number, campaign: CampaignFormData): Observable<Campaign> {
    return this.http.put<Campaign>(`${this.apiUrl}/${id}`, campaign);
  }

  updateCampaignStatus(id: number, status: CampaignStatus): Observable<Campaign> {
    return this.http.patch<Campaign>(`${this.apiUrl}/${id}/status/${status}`, {});
  }

  deleteCampaign(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getCampaignsByStatus(status: CampaignStatus): Observable<Campaign[]> {
    if (!this.isBrowser()) {
      return of(this.mockCampaigns.filter(c => c.status === status));
    }

    return this.http.get<Campaign[]>(`${this.apiUrl}/status/${status}`);
  }
}
