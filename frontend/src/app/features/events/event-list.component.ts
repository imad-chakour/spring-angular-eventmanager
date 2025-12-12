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

    this.eventService.getEvents().subscribe({
      next: (events) => {
        this.events.set(events as Event[]);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Erreur lors du chargement des événements');
        this.isLoading.set(false);
        console.error('Error loading events:', error);
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


