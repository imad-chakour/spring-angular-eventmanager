import { Component, inject, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  private authService = inject(AuthService);

  currentUser = signal(this.authService.currentUser());
  isAuthenticated = signal(this.authService.isAuthenticated());

  constructor() {
    // Mettre à jour les signaux quand l'authentification change
    effect(() => {
      this.currentUser.set(this.authService.currentUser());
      this.isAuthenticated.set(this.authService.isAuthenticated());
    });
  }

  shortcuts = [
    {
      title: 'Événements',
      description: 'Gérer vos événements et inscriptions',
      icon: '📅',
      route: '/events',
      color: '#667eea',
      service: 'Event Service'
    },
    {
      title: 'Campagnes',
      description: 'Gérer vos campagnes marketing',
      icon: '📢',
      route: '/campaigns',
      color: '#764ba2',
      service: 'Campaign Service'
    },
    {
      title: 'Analytics',
      description: 'Voir les statistiques et analyses',
      icon: '📊',
      route: '/analytics',
      color: '#f093fb',
      service: 'Analytics Service'
    },
    {
      title: 'Notifications',
      description: 'Consulter vos notifications',
      icon: '🔔',
      route: '/notifications',
      color: '#4facfe',
      service: 'Notification Service'
    }
  ];

  adminShortcuts = [
    {
      title: 'Utilisateurs',
      description: 'Gérer les utilisateurs du système',
      icon: '👥',
      route: '/users',
      color: '#43e97b',
      service: 'User Service'
    }
  ];

  isAdmin(): boolean {
    const user = this.currentUser();
    return user?.role === 'ADMIN';
  }
}
