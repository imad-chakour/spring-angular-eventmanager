import { Component, OnInit } from '@angular/core';
import { CommonModule, NgClass } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { User, UserStatus } from '../../models/user.model';
import { UserService } from '../../services/user.service';
import {ReplacePipe} from '../../../../shared/pipes/replace.pipe';


@Component({
  selector: 'app-user-detail',
  standalone: true,
  imports: [CommonModule, NgClass, RouterModule, ReplacePipe, ReplacePipe],
  templateUrl: './user-detail.component.html'
})
export class UserDetailComponent implements OnInit {
  user: User | null = null;
  isLoading = false;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error = 'Invalid user ID';
      return;
    }

    this.isLoading = true;
    this.userService.getUser(+id).subscribe({
      next: (user) => {
        this.user = user;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load user details';
        this.isLoading = false;
        console.error('Error loading user:', err);
      }
    });
  }

  getStatusClass(status: UserStatus): string {
    switch (status) {
      case 'ACTIVE': return 'badge bg-success';
      case 'INACTIVE': return 'badge bg-secondary';
      case 'SUSPENDED': return 'badge bg-warning text-dark';
      default: return 'badge bg-secondary';
    }
  }

  onEdit(): void {
    if (this.user) {
      this.router.navigate(['/users', this.user.id, 'edit']);
    }
  }

  onBack(): void {
    this.router.navigate(['/users']);
  }
}
