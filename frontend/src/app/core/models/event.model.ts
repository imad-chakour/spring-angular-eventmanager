export enum EventType {
  WEBINAIRE = 'WEBINAIRE',
  SALON = 'SALON',
  PROMOTION = 'PROMOTION',
  CONFERENCE = 'CONFERENCE',
  ATELIER = 'ATELIER'
}

export enum EventFormat {
  VIRTUEL = 'VIRTUEL',
  PHYSIQUE = 'PHYSIQUE',
  HYBRIDE = 'HYBRIDE'
}

export enum EventStatus {
  PLANIFIED = 'PLANIFIED',
  ACTIF = 'ACTIF',
  TERMINE = 'TERMINE',
  ANNULE = 'ANNULE',
  CLOTURE = 'CLOTURE'
}

export interface Event {
  id?: number;
  eventId: string;
  title: string;
  description?: string;
  type: EventType;
  format?: EventFormat;
  startDate: string;
  endDate: string;
  location?: string;
  maxCapacity?: number;
  currentParticipants?: number;
  status?: EventStatus;
  organizerId: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface Registration {
  id?: number;
  eventId: number;
  userId: number;  // Changed from participantId to userId
  registrationDate?: string;
  status?: RegistrationStatus;
}

export enum RegistrationStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED'
}
