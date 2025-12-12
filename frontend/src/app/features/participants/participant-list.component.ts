import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { User, UserRole, UserStatus } from '../../core/models/user.model';

@Component({
  selector: 'app-participant-list',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe],
  templateUrl: './participant-list.component.html',
  styleUrl: './participant-list.component.css',
})
export class ParticipantListComponent implements OnInit {
  private userService = inject(UserService);

  participants = signal<User[]>([]);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);

  readonly UserStatus = UserStatus;
  readonly UserRole = UserRole;

  ngOnInit(): void {
    this.loadParticipants();
  }

  loadParticipants(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    // Load all users and filter for PARTICIPANT role
    this.userService.getUsers().subscribe({
      next: (users) => {
        console.log('Users loaded:', users);
        // Ensure users is an array
        const usersArray = Array.isArray(users) ? users : [];
        // Filter users with PARTICIPANT role
        const participants = usersArray.filter(user => user.role === UserRole.PARTICIPANT);
        console.log('Participants filtered:', participants);
        this.participants.set(participants);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading participants:', error);
        console.error('Error details:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          error: error.error
        });
        this.errorMessage.set(
          error.error?.error || 
          error.error?.message || 
          `Erreur lors du chargement des participants (${error.status || 'Unknown'})`
        );
        this.isLoading.set(false);
      }
    });
  }

  deleteParticipant(id: number | undefined, email: string): void {
    if (!id) return;

    if (confirm(`Êtes-vous sûr de vouloir supprimer le participant ${email} ?`)) {
      this.userService.deleteUser(id).subscribe({
        next: () => {
          this.loadParticipants();
        },
        error: (error) => {
          alert('Erreur lors de la suppression du participant');
          console.error('Error deleting participant:', error);
        }
      });
    }
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
}


