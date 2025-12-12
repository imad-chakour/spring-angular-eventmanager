import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NotificationService } from '../../core/services/notification.service';
import { Notification, NotificationStatus, NotificationType, NotificationChannel } from '../../core/models/notification.model';

@Component({
  selector: 'app-notification-list',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe, FormsModule],
  templateUrl: './notification-list.component.html',
  styleUrl: './notification-list.component.css'
})
export class NotificationListComponent implements OnInit {
  private notificationService = inject(NotificationService);

  notifications = signal<Notification[]>([]);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);
  selectedStatus: NotificationStatus | 'ALL' = 'ALL';

  readonly NotificationStatus = NotificationStatus;
  readonly NotificationType = NotificationType;
  readonly NotificationChannel = NotificationChannel;

  statusOptions: { value: NotificationStatus | 'ALL', label: string }[] = [
    { value: 'ALL', label: 'Toutes' },
    { value: NotificationStatus.PENDING, label: 'En attente' },
    { value: NotificationStatus.SENT, label: 'Envoyées' },
    { value: NotificationStatus.DELIVERED, label: 'Livrées' },
    { value: NotificationStatus.FAILED, label: 'Échouées' },
    { value: NotificationStatus.RETRY, label: 'Nouvelle tentative' }
  ];

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    const request = this.selectedStatus === 'ALL'
      ? this.notificationService.getNotifications()
      : this.notificationService.getNotificationsByStatus(this.selectedStatus);

    request.subscribe({
      next: (notifications) => {
        this.notifications.set(notifications);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Erreur lors du chargement des notifications');
        this.isLoading.set(false);
        console.error('Error loading notifications:', error);
      }
    });
  }

  onStatusFilterChange(): void {
    this.loadNotifications();
  }

  deleteNotification(id: number | undefined, subject: string): void {
    if (!id) return;

    if (confirm(`Êtes-vous sûr de vouloir supprimer la notification "${subject}" ?`)) {
      this.notificationService.deleteNotification(id).subscribe({
        next: () => {
          this.loadNotifications();
        },
        error: (error) => {
          alert('Erreur lors de la suppression de la notification');
          console.error('Error deleting notification:', error);
        }
      });
    }
  }

  triggerBatchProcessing(): void {
    if (confirm('Déclencher le traitement par lots des notifications en attente ?')) {
      this.notificationService.triggerBatchProcessing().subscribe({
        next: (response) => {
          alert(response.message || 'Traitement par lots déclenché avec succès');
          this.loadNotifications();
        },
        error: (error) => {
          alert('Erreur lors du déclenchement du traitement par lots');
          console.error('Error triggering batch:', error);
        }
      });
    }
  }

  getStatusBadgeClass(status: NotificationStatus | undefined): string {
    if (!status) return 'badge-default';
    const classes: Record<NotificationStatus, string> = {
      [NotificationStatus.PENDING]: 'badge-pending',
      [NotificationStatus.SENT]: 'badge-sent',
      [NotificationStatus.DELIVERED]: 'badge-delivered',
      [NotificationStatus.FAILED]: 'badge-failed',
      [NotificationStatus.RETRY]: 'badge-retry'
    };
    return classes[status] || 'badge-default';
  }
}
