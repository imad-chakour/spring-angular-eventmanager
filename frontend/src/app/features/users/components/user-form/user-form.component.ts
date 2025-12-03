import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { User, UserRole, UserStatus } from '../../models/user.model';
import { UserService } from '../../services/user.service';
import {ReplacePipe} from '../../../../shared/pipes/replace.pipe';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, ReplacePipe, ReplacePipe],
  templateUrl: './user-form.component.html'
})
export class UserFormComponent implements OnInit {
  userForm: FormGroup;
  isEditMode = false;
  userId: number | null = null;
  isLoading = false;
  error: string | null = null;
  roles = Object.values(UserRole);
  statuses = Object.values(UserStatus);

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.userForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      role: [UserRole.PARTICIPANT, Validators.required],
      status: [UserStatus.ACTIVE, Validators.required],
      password: ['', [Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.userId = +id;
        this.loadUser(this.userId);
      }
    });
  }

  loadUser(id: number): void {
    this.isLoading = true;
    this.userService.getUser(id).subscribe({
      next: (user) => {
        this.userForm.patchValue({
          firstName: user.firstName,
          lastName: user.lastName,
          email: user.email,
          role: user.role,
          status: user.status
        });
        this.userForm.get('password')?.clearValidators();
        this.userForm.get('password')?.updateValueAndValidity();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load user data';
        this.isLoading = false;
        console.error('Error loading user:', err);
      }
    });
  }

  onSubmit(): void {
    if (this.userForm.invalid) {
      return;
    }

    this.isLoading = true;
    const userData = { ...this.userForm.value };

    // Remove password if it's empty (for updates)
    if (!userData.password) {
      delete userData.password;
    }

    const request = this.isEditMode && this.userId
      ? this.userService.updateUser(this.userId, userData)
      : this.userService.createUser(userData);

    request.subscribe({
      next: () => {
        this.router.navigate(['/users']);
      },
      error: (err) => {
        this.error = this.isEditMode
          ? 'Failed to update user. Please try again.'
          : 'Failed to create user. Please try again.';
        this.isLoading = false;
        console.error('Error saving user:', err);
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/users']);
  }

  get f() {
    return this.userForm.controls;
  }
}
