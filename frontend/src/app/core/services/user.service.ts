import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { getApiUrl } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private readonly apiUrl = getApiUrl('/api/users');

  // Headers pour désactiver le cache HTTP
  private readonly noCacheHeaders = new HttpHeaders({
    'Cache-Control': 'no-cache, no-store, must-revalidate',
    'Pragma': 'no-cache',
    'Expires': '0'
  });

  getUsers(forceRefresh: boolean = false): Observable<User[]> {
    // Ajouter un timestamp pour forcer le rafraîchissement
    const url = forceRefresh 
      ? `${this.apiUrl}?_t=${Date.now()}` 
      : this.apiUrl;
    return this.http.get<User[]>(url, { headers: this.noCacheHeaders });
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  getUserByEmail(email: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/email/${email}`);
  }

  createUser(user: User): Observable<User> {
    return this.http.post<User>(this.apiUrl, user);
  }

  updateUser(id: number, user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, user);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updateLastLogin(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/last-login`, {});
  }
}
