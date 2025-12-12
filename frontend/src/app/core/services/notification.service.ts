import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Notification, NotificationStatus } from '../models/notification.model';
import { getApiUrl } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private http = inject(HttpClient);
  private readonly apiUrl = getApiUrl('/api/notifications');

  getNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(this.apiUrl);
  }

  getNotificationById(id: number): Observable<Notification> {
    return this.http.get<Notification>(`${this.apiUrl}/${id}`);
  }

  getNotificationsByStatus(status: NotificationStatus): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/status/${status}`);
  }

  getNotificationsByRecipient(recipientId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/recipient/${recipientId}`);
  }

  createNotification(notification: Notification): Observable<Notification> {
    return this.http.post<Notification>(this.apiUrl, notification);
  }

  updateNotification(id: number, notification: Notification): Observable<Notification> {
    return this.http.put<Notification>(`${this.apiUrl}/${id}`, notification);
  }

  updateNotificationStatus(id: number, status: NotificationStatus): Observable<Notification> {
    return this.http.patch<Notification>(`${this.apiUrl}/${id}/status/${status}`, {});
  }

  deleteNotification(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  triggerBatchProcessing(): Observable<{ status: string; message: string }> {
    return this.http.post<{ status: string; message: string }>(`${this.apiUrl}/batch/process`, {});
  }
}
