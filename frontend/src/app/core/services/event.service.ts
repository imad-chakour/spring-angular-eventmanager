import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Event, Registration } from '../models/event.model';
import { getApiUrl } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class EventService {
  private http = inject(HttpClient);
  private readonly apiUrl = getApiUrl('/api/events');

  getEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(this.apiUrl);
  }

  getEventById(id: number): Observable<Event> {
    return this.http.get<Event>(`${this.apiUrl}/${id}`);
  }

  getEventsByOrganizer(organizerId: number): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.apiUrl}/organizer/${organizerId}`);
  }

  getEventsByStatus(status: string): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.apiUrl}/status/${status}`);
  }

  createEvent(event: Event): Observable<Event> {
    return this.http.post<Event>(this.apiUrl, event);
  }

  updateEvent(id: number, event: Event): Observable<Event> {
    return this.http.put<Event>(`${this.apiUrl}/${id}`, event);
  }

  closeEvent(id: number): Observable<Event> {
    return this.http.patch<Event>(`${this.apiUrl}/${id}/close`, {});
  }

  deleteEvent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Registration methods
  getRegistrations(): Observable<Registration[]> {
    return this.http.get<Registration[]>(`${this.apiUrl}/registrations`);
  }

  getRegistrationsByEvent(eventId: number): Observable<Registration[]> {
    return this.http.get<Registration[]>(`${this.apiUrl}/registrations/event/${eventId}`);
  }

  getRegistrationsByUser(userId: number): Observable<Registration[]> {
    return this.http.get<Registration[]>(`${this.apiUrl}/registrations/user/${userId}`);
  }

  registerUser(eventId: number, userId: number): Observable<Registration> {
    return this.http.post<Registration>(
      `${this.apiUrl}/registrations/event/${eventId}/user/${userId}`,
      {}
    );
  }

  deleteRegistration(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/registrations/${id}`);
  }
}
