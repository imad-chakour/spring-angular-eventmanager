import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { EventService } from '../../core/services/event.service';
import { Event, EventType, EventFormat, EventStatus } from '../../core/models/event.model';

@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe],
  templateUrl: './event-list.component.html',
  styleUrl: './event-list.component.css',
})
export class EventListComponent implements OnInit {
  private eventService = inject(EventService);

  events = signal<Event[]>([]);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);

  readonly EventType = EventType;
  readonly EventFormat = EventFormat;
  readonly EventStatus = EventStatus;

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    console.log('=== EventListComponent: Loading events ===');

    this.eventService.getEvents().subscribe({
      next: (events) => {
        console.log('Events received:', events);
        console.log('Events type:', typeof events, Array.isArray(events));
        console.log('Events count:', Array.isArray(events) ? events.length : 0);
        
        // Ensure data is an array
        const eventsArray = Array.isArray(events) ? events : [];
        console.log('Setting events array with', eventsArray.length, 'items');
        
        // Validate event structure
        if (eventsArray.length > 0) {
          console.log('First event structure:', eventsArray[0]);
        }
        
        this.events.set(eventsArray);
        this.isLoading.set(false);
        this.errorMessage.set(null);
      },
      error: (error) => {
        console.error('=== EventListComponent: Error loading events ===');
        console.error('Error details:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          error: error.error
        });
        const errorMsg = error.status 
          ? `Erreur ${error.status}: ${error.statusText || error.message}`
          : `Erreur lors du chargement: ${error.message || 'Erreur inconnue'}`;
        this.errorMessage.set(errorMsg);
        this.isLoading.set(false);
        this.events.set([]);
      }
    });
  }

  deleteEvent(id: number | undefined, title: string): void {
    if (!id) return;

    if (confirm(`Êtes-vous sûr de vouloir supprimer l'événement "${title}" ?`)) {
      this.eventService.deleteEvent(id).subscribe({
        next: () => {
          this.loadEvents();
        },
        error: (error) => {
          alert('Erreur lors de la suppression de l\'événement');
          console.error('Error deleting event:', error);
        }
      });
    }
  }

  getStatusBadgeClass(status: EventStatus | undefined): string {
    if (!status) return 'badge-default';
    const classes: Record<EventStatus, string> = {
      [EventStatus.PLANIFIED]: 'badge-planned',
      [EventStatus.ACTIF]: 'badge-active',
      [EventStatus.TERMINE]: 'badge-completed',
      [EventStatus.ANNULE]: 'badge-cancelled',
      [EventStatus.CLOTURE]: 'badge-closed'
    };
    return classes[status] || 'badge-default';
  }
}


