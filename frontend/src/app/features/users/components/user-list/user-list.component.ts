import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { UserService } from '../../../../core/services/user.service';
import { AuthService } from '../../../../core/auth.service';
import { User, UserRole, UserStatus } from '../../../../core/models/user.model';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css'
})
export class UserListComponent implements OnInit {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private router = inject(Router);

  users = signal<User[]>([]);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);
  currentUser = this.authService.currentUser;

  readonly UserRole = UserRole;
  readonly UserStatus = UserStatus;

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(forceRefresh: boolean = false): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.userService.getUsers(forceRefresh).subscribe({
      next: (users) => {
        console.log('Users loaded:', users);
        // Ensure users is an array
        const usersArray = Array.isArray(users) ? users : [];
        this.users.set(usersArray);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading users:', error);
        console.error('Error details:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          error: error.error
        });
        this.errorMessage.set(
          error.error?.error || 
          error.error?.message || 
          `Erreur lors du chargement des utilisateurs (${error.status || 'Unknown'})`
        );
        this.isLoading.set(false);
      }
    });
  }

  refreshUsers(): void {
    console.log('🔄 Rafraîchissement forcé de la liste des utilisateurs...');
    this.loadUsers(true);
  }

  deleteUser(id: number | undefined, email: string): void {
    if (!id) return;

    if (confirm(`Êtes-vous sûr de vouloir supprimer l'utilisateur ${email} ?`)) {
      this.userService.deleteUser(id).subscribe({
        next: () => {
          this.loadUsers();
        },
        error: (error) => {
          alert('Erreur lors de la suppression de l\'utilisateur');
          console.error('Error deleting user:', error);
        }
      });
    }
  }

  getRoleBadgeClass(role: UserRole): string {
    const classes: Record<UserRole, string> = {
      [UserRole.ADMIN]: 'badge-admin',
      [UserRole.MARKETING_MANAGER]: 'badge-manager',
      [UserRole.MARKETING_USER]: 'badge-user',
      [UserRole.PARTICIPANT]: 'badge-participant'
    };
    return classes[role] || 'badge-default';
  }

  getStatusBadgeClass(status: UserStatus | undefined): string {
    if (!status) return 'badge-default';
    const classes: Record<UserStatus, string> = {
      [UserStatus.ACTIVE]: 'badge-active',
      [UserStatus.INACTIVE]: 'badge-inactive',
      [UserStatus.SUSPENDED]: 'badge-suspended'
    };
    return classes[status] || 'badge-default';
  }

  canEdit(): boolean {
    return this.authService.isAdmin() || this.authService.isMarketingManager();
  }

  canDelete(): boolean {
    return this.authService.isAdmin();
  }
}
