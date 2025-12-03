import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

interface LoginResponse {
  token: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private readonly tokenKey = 'auth_token';
  readonly isAuthenticated = signal<boolean>(!!this.getToken());

  login(email: string, password: string) {
    return this.http
      .post<LoginResponse>('http://localhost:7020/api/users/login', { email, password })
      .subscribe({
        next: (res) => {
          this.setToken(res.token);
          this.isAuthenticated.set(true);
          this.router.navigate(['/users']);
        },
        error: () => {
          this.isAuthenticated.set(false);
        },
      });
  }

  register(data: { email: string; password: string; firstName?: string; lastName?: string }) {
    return this.http.post('http://localhost:7020/api/users/register', data);
  }

  logout() {
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      localStorage.removeItem(this.tokenKey);
    }
    this.isAuthenticated.set(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
      return null;
    }
    return localStorage.getItem(this.tokenKey);
  }

  private setToken(token: string) {
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
      return;
    }
    localStorage.setItem(this.tokenKey, token);
  }
}

