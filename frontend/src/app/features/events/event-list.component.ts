import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { EventService } from '../../core/services/event.service';
import { AuthService } from '../../core/auth.service';
import { Event, EventType, EventFormat, EventStatus } from '../../core/models/event.model';
import { EventFormComponent } from './event-form.component';

@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [CommonModule, DatePipe, EventFormComponent],
  templateUrl: './event-list.component.html',
  styleUrl: './event-list.component.css',
})
export class EventListComponent implements OnInit {
  private eventService = inject(EventService);
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  events = signal<Event[]>([]);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);
  isCreating = signal<boolean>(false);
  selectedEvent = signal<Event | null>(null);

  readonly EventType = EventType;
  readonly EventFormat = EventFormat;
  readonly EventStatus = EventStatus;

  ngOnInit(): void {
    this.loadEvents();
  }
  
  startCreate(): void {
    this.isCreating.set(true);
    this.selectedEvent.set(null);
  }
  
  editEvent(event: Event): void {
    this.selectedEvent.set(event);
    this.isCreating.set(false);
  }
  
  onFormCancel(): void {
    this.isCreating.set(false);
    this.selectedEvent.set(null);
  }
  
  onFormSubmit(): void {
    this.isCreating.set(false);
    this.selectedEvent.set(null);
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

    if (confirm(`⚠ ATTENTION : Êtes-vous sûr de vouloir SUPPRIMER DÉFINITIVEMENT l'événement "${title}" ?\n\nCette action est irréversible. L'événement et toutes ses inscriptions seront supprimés de la base de données.`)) {
      console.log('=== Suppression définitive d\'événement ===');
      console.log('Event ID:', id);
      console.log('Event Title:', title);
      
      this.eventService.deleteEvent(id).subscribe({
        next: () => {
          console.log('Événement supprimé définitivement avec succès');
          this.loadEvents();
        },
        error: (error) => {
          console.error('=== Erreur lors de la suppression de l\'événement ===');
          console.error('Error details:', {
            status: error.status,
            statusText: error.statusText,
            message: error.message,
            error: error.error
          });
          
          let errorMsg = 'Échec de la suppression de l\'événement';
          if (error.error) {
            if (error.error.message) {
              errorMsg = error.error.message;
            } else if (typeof error.error === 'string') {
              errorMsg = error.error;
            } else if (error.error.error) {
              errorMsg = error.error.error + (error.error.message ? ': ' + error.error.message : '');
            }
          } else if (error.message) {
            errorMsg = error.message;
          }
          
          alert(`Erreur: ${errorMsg}`);
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

  registerForEvent(event: Event): void {
    if (!event.id) return;

    // Récupérer l'ID de l'utilisateur connecté depuis le service d'authentification
    const currentUser = this.authService.currentUser();
    if (!currentUser || !currentUser.id) {
      alert('Vous devez être connecté pour vous inscrire à un événement');
      return;
    }

    if (confirm(`Voulez-vous vous inscrire à l'événement "${event.title}" ?`)) {
      this.eventService.registerUser(event.id, currentUser.id).subscribe({
        next: (registration) => {
          console.log('Registration successful:', registration);
          alert('Inscription réussie !');
          this.loadEvents(); // Recharger pour mettre à jour le nombre de participants
        },
        error: (error) => {
          console.error('Error registering for event:', error);
          const errorMsg = error.error?.message || error.message || 'Erreur lors de l\'inscription';
          alert(`Erreur: ${errorMsg}`);
        }
      });
    }
  }

  canRegister(event: Event): boolean {
    // Vérifier si l'utilisateur peut s'inscrire
    if (!event.id) return false;
    
    // Vérifier la capacité
    if (event.maxCapacity && event.currentParticipants && 
        event.currentParticipants >= event.maxCapacity) {
      return false; // Événement complet
    }
    
    // Vérifier le statut
    if (event.status === 'TERMINE' || event.status === 'ANNULE' || event.status === 'CLOTURE') {
      return false; // Événement terminé ou annulé
    }
    
    return true;
  }
}


