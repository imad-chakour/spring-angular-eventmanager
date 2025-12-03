import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

interface Event {
  id: number;
  eventId: string;
  title: string;
  description: string;
  status: string;
  organizerId: number;
}

@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './event-list.component.html',
  styleUrl: './event-list.component.css',
})
export class EventListComponent implements OnInit {
  private http = inject(HttpClient);
  events: Event[] = [];

  ngOnInit(): void {
    this.http.get<Event[]>('http://localhost:9030/api/events').subscribe((data) => {
      this.events = data as Event[];
    });
  }
}


