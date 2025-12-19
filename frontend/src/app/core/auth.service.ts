import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, of } from 'rxjs';
import { LoginRequest, LoginResponse, RegisterRequest, User } from './models/user.model';
import { getApiUrl } from './config/api.config';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private readonly apiUrl = getApiUrl('/api/users');
  private readonly tokenKey = 'auth_token';
  private readonly userKey = 'current_user';

  readonly isAuthenticated = signal<boolean>(!!this.getToken());
  readonly currentUser = signal<User | null>(this.getCurrentUser());

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => {
        this.setToken(response.token);
        this.isAuthenticated.set(true);
        // Récupérer les informations utilisateur
        this.loadUserInfo(response.email);
        this.router.navigate(['/']);
      }),
      catchError((error) => {
        this.isAuthenticated.set(false);
        throw error;
      })
    );
  }

  register(data: RegisterRequest): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/register`, data);
  }

  logout(): void {
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      localStorage.removeItem(this.tokenKey);
      localStorage.removeItem(this.userKey);
    }
    this.isAuthenticated.set(false);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
      return null;
    }
    return localStorage.getItem(this.tokenKey);
  }

  getCurrentUser(): User | null {
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
      return null;
    }
    const userStr = localStorage.getItem(this.userKey);
    return userStr ? JSON.parse(userStr) : null;
  }

  private setToken(token: string): void {
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
      return;
    }
    localStorage.setItem(this.tokenKey, token);
  }

  private loadUserInfo(email: string): void {
    this.http.get<User>(`${this.apiUrl}/email/${email}`).subscribe({
      next: (user) => {
        this.currentUser.set(user);
        if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
          localStorage.setItem(this.userKey, JSON.stringify(user));
        }
      },
      error: (err) => {
        console.error('Error loading user info:', err);
      }
    });
  }

  hasRole(role: string): boolean {
    const user = this.currentUser();
    return user?.role === role;
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  isMarketingManager(): boolean {
    return this.hasRole('MARKETING_MANAGER');
  }
}

