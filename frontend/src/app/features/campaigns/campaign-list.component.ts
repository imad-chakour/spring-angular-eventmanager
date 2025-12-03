import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

interface Campaign {
  id: number;
  reference: string;
  name: string;
  status: string;
  organizerId: number;
}

@Component({
  selector: 'app-campaign-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './campaign-list.component.html',
  styleUrl: './campaign-list.component.css',
})
export class CampaignListComponent implements OnInit {
  private http = inject(HttpClient);
  campaigns: Campaign[] = [];

  ngOnInit(): void {
    this.http.get<Campaign[]>('http://localhost:9020/api/campaigns').subscribe((data) => {
      this.campaigns = data as Campaign[];
    });
  }
}


