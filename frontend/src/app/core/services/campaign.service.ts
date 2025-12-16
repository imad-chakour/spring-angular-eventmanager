import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';
import { getApiUrl } from '../config/api.config';

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
  private apiUrl = getApiUrl('/api/campaigns');

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
    if (!this.isBrowser()) {
      return of(this.mockCampaigns);
    }
    console.log('=== CampaignService: getAllCampaigns ===');
    console.log('API URL:', this.apiUrl);
    console.log('Is browser:', this.isBrowser());
    
    return this.http.get<Campaign[]>(this.apiUrl, {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    }).pipe(
      catchError((error) => {
        console.error('=== CampaignService: Error fetching campaigns ===');
        console.error('URL:', this.apiUrl);
        console.error('Status:', error.status);
        console.error('StatusText:', error.statusText);
        console.error('Message:', error.message);
        console.error('Error name:', error.name);
        console.error('Error object:', error);
        
        if (error.status === 0 || !error.status) {
          console.error('⚠️ STATUS 0 DETECTED - This is a network/CORS error!');
          console.error('Possible causes:');
          console.error('1. Service is not running (check campaignservice on port 9020)');
          console.error('2. Gateway is not running (check reactivegateway on port 1111)');
          console.error('3. CORS is blocking the request (check CorsConfig in gateway)');
          console.error('4. Network connectivity issue');
          console.error('5. Token is missing or invalid (check localStorage)');
        }
        
        return throwError(() => error);
      })
    );
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
