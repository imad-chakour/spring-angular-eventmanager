import { Component, Input, Output, EventEmitter, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EventService } from '../../core/services/event.service';
import { AuthService } from '../../core/auth.service';
import { Event, EventType, EventFormat, EventStatus } from '../../core/models/event.model';

export interface EventFormData {
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  type: EventType;
  format: EventFormat;
  location: string;
  maxCapacity: number;
  organizerId: number;
  status?: EventStatus;
}

@Component({
  selector: 'app-event-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './event-form.component.html',
  styleUrls: ['./event-form.component.css']
})
export class EventFormComponent implements OnInit {
  private eventService = inject(EventService);
  private authService = inject(AuthService);

  @Input() event: Event | null = null;
  @Output() cancel = new EventEmitter<void>();
  @Output() submit = new EventEmitter<void>();

  isEditing = false;
  formData: EventFormData = {
    title: '',
    description: '',
    startDate: '',
    endDate: '',
    type: EventType.CONFERENCE,
    format: EventFormat.PHYSIQUE,
    location: '',
    maxCapacity: 0,
    organizerId: 0
  };

  typeOptions: { value: EventType, label: string }[] = [
    { value: EventType.WEBINAIRE, label: 'Webinaire' },
    { value: EventType.SALON, label: 'Salon' },
    { value: EventType.PROMOTION, label: 'Promotion' },
    { value: EventType.CONFERENCE, label: 'Conférence' },
    { value: EventType.ATELIER, label: 'Atelier' }
  ];

  formatOptions: { value: EventFormat, label: string }[] = [
    { value: EventFormat.VIRTUEL, label: 'Virtuel' },
    { value: EventFormat.PHYSIQUE, label: 'Physique' },
    { value: EventFormat.HYBRIDE, label: 'Hybride' }
  ];

  ngOnInit(): void {
    // Définir l'organizerId depuis l'utilisateur connecté
    const currentUser = this.authService.currentUser();
    if (currentUser && currentUser.id) {
      this.formData.organizerId = currentUser.id;
      console.log('Organizer ID défini depuis l\'utilisateur connecté:', this.formData.organizerId);
    } else {
      console.warn('⚠ Aucun utilisateur connecté trouvé, organizerId sera 0');
    }
    
    if (this.event) {
      this.isEditing = true;
      this.formData = {
        title: this.event.title,
        description: this.event.description || '',
        startDate: this.event.startDate.split('T')[0],
        endDate: this.event.endDate.split('T')[0],
        type: this.event.type,
        format: this.event.format || EventFormat.PHYSIQUE,
        location: this.event.location || '',
        maxCapacity: this.event.maxCapacity || 0,
        organizerId: this.event.organizerId,
        status: this.event.status
      };
    }
  }

  onSubmit(): void {
    // Validate dates
    const startDate = new Date(this.formData.startDate);
    const endDate = new Date(this.formData.endDate);

    if (endDate <= startDate) {
      alert('La date de fin doit être après la date de début');
      return;
    }

    // Validation de l'organizerId
    if (!this.formData.organizerId || this.formData.organizerId === 0) {
      const currentUser = this.authService.currentUser();
      if (currentUser && currentUser.id) {
        this.formData.organizerId = currentUser.id;
      } else {
        alert('Erreur: Vous devez être connecté pour créer un événement');
        return;
      }
    }
    
    // Convert dates to ISO format for backend (LocalDateTime)
    // Format: yyyy-MM-ddTHH:mm:ss
    const startDateStr = this.formData.startDate + 'T00:00:00';
    const endDateStr = this.formData.endDate + 'T23:59:59';
    
    // Créer l'objet au format attendu par EventCreateRequest DTO
    // Note: eventId sera généré par le backend pour les nouveaux événements
    // On utilise Partial<Event> car le backend attend EventCreateRequest qui n'a pas tous les champs
    const eventData: Partial<Event> = {
      title: this.formData.title.trim(),
      description: this.formData.description?.trim() || '',
      type: this.formData.type,
      format: this.formData.format,
      startDate: startDateStr,
      endDate: endDateStr,
      location: this.formData.location?.trim() || '',
      maxCapacity: this.formData.maxCapacity || undefined,
      organizerId: this.formData.organizerId,
      status: this.formData.status || (this.isEditing && this.event ? this.event.status : EventStatus.PLANIFIED)
    };
    
    console.log('Dates formatées:');
    console.log('  startDate:', startDateStr);
    console.log('  endDate:', endDateStr);
    console.log('=== EventForm: Submitting event ===');
    console.log('Is editing:', this.isEditing);
    console.log('Event data:', eventData);

    if (this.isEditing && this.event && this.event.id) {
      this.eventService.updateEvent(this.event.id, eventData).subscribe({
        next: (updatedEvent) => {
          console.log('Event updated successfully:', updatedEvent);
          alert('Événement mis à jour avec succès !');
          this.submit.emit();
        },
        error: (error) => {
          console.error('=== EventForm: Error updating event ===');
          console.error('Error details:', {
            status: error.status,
            statusText: error.statusText,
            message: error.message,
            error: error.error
          });
          
          let errorMsg = 'Échec de la mise à jour de l\'événement';
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
    } else {
      this.eventService.createEvent(eventData).subscribe({
        next: (createdEvent) => {
          console.log('Event created successfully:', createdEvent);
          alert('Événement créé avec succès !');
          this.submit.emit();
        },
        error: (error) => {
          console.error('=== EventForm: Error creating event ===');
          console.error('Error details:', {
            status: error.status,
            statusText: error.statusText,
            message: error.message,
            error: error.error
          });
          
          let errorMsg = 'Échec de la création de l\'événement';
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

  onCancel(): void {
    this.cancel.emit();
  }
}
