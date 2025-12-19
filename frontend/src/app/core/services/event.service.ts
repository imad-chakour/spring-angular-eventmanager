import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Event, Registration } from '../models/event.model';
import { getApiUrl } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class EventService {
  private http = inject(HttpClient);
  private readonly apiUrl = getApiUrl('/api/events');

  getEvents(): Observable<Event[]> {
    console.log('=== EventService: getEvents ===');
    console.log('API URL:', this.apiUrl);
    return this.http.get<Event[]>(this.apiUrl).pipe(
      catchError((error) => {
        console.error('=== EventService: Error fetching events ===');
        console.error('URL:', this.apiUrl);
        console.error('Status:', error.status);
        console.error('StatusText:', error.statusText);
        console.error('Message:', error.message);
        console.error('Error object:', error);
        return throwError(() => error);
      })
    );
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

  createEvent(event: Partial<Event>): Observable<Event> {
    console.log('=== EventService: createEvent ===');
    console.log('Event data:', event);
    return this.http.post<Event>(this.apiUrl, event).pipe(
      catchError((error) => {
        console.error('=== EventService: Error creating event ===');
        console.error('Error details:', error);
        return throwError(() => error);
      })
    );
  }

  updateEvent(id: number, event: Partial<Event>): Observable<Event> {
    console.log('=== EventService: updateEvent ===');
    console.log('Event ID:', id);
    console.log('Event data:', event);
    return this.http.put<Event>(`${this.apiUrl}/${id}`, event).pipe(
      catchError((error) => {
        console.error('=== EventService: Error updating event ===');
        console.error('Error details:', error);
        return throwError(() => error);
      })
    );
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
