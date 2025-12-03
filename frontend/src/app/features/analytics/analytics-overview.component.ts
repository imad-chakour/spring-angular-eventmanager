import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

interface EventMetrics {
  id: number;
  eventId: number;
  totalRegistrations: number;
  actualAttendance: number;
  attendanceRate: number;
}

@Component({
  selector: 'app-analytics-overview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './analytics-overview.component.html',
  styleUrl: './analytics-overview.component.css',
})
export class AnalyticsOverviewComponent implements OnInit {
  private http = inject(HttpClient);
  metrics: EventMetrics[] = [];

  ngOnInit(): void {
    this.http
      .get<EventMetrics[]>('http://localhost:9010/api/analytics/events')
      .subscribe((data) => {
        this.metrics = data as EventMetrics[];
      });
  }
}


