import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../../core/services/user.service';
import { User, UserRole, UserStatus } from '../../../../core/models/user.model';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './user-form.component.html',
  styleUrl: './user-form.component.css'
})
export class UserFormComponent implements OnInit {
  private userService = inject(UserService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  userForm: FormGroup;
  isEditMode = signal<boolean>(false);
  userId = signal<number | null>(null);
  isLoading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  readonly userRoles = Object.values(UserRole);
  readonly userStatuses = Object.values(UserStatus);

  constructor() {
    this.userForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      firstName: [''],
      lastName: [''],
      role: [UserRole.PARTICIPANT, Validators.required],
      status: [UserStatus.ACTIVE, Validators.required],
      // Marketing fields
      phone: [''],
      company: [''],
      jobTitle: [''],
      optInMarketing: [true],
      password: [''],
      confirmPassword: ['']
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode.set(true);
      this.userId.set(+id);
      this.loadUser(+id);
      // En mode édition, le mot de passe n'est pas requis
      this.userForm.get('password')?.clearValidators();
      this.userForm.get('confirmPassword')?.clearValidators();
    } else {
      // En mode création, le mot de passe est requis
      this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
      this.userForm.get('confirmPassword')?.setValidators([Validators.required]);
    }
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value && confirmPassword.value) {
      if (password.value !== confirmPassword.value) {
        confirmPassword.setErrors({ passwordMismatch: true });
        return { passwordMismatch: true };
      }
    }
    return null;
  }

  loadUser(id: number): void {
    this.isLoading.set(true);
    this.userService.getUserById(id).subscribe({
      next: (user) => {
        this.userForm.patchValue({
          email: user.email,
          firstName: user.firstName || '',
          lastName: user.lastName || '',
          role: user.role,
          status: user.status || UserStatus.ACTIVE,
          phone: user.phone || '',
          company: user.company || '',
          jobTitle: user.jobTitle || '',
          optInMarketing: user.optInMarketing !== undefined ? user.optInMarketing : true
        });
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Erreur lors du chargement de l\'utilisateur');
        this.isLoading.set(false);
        console.error('Error loading user:', error);
      }
    });
  }

  onSubmit(): void {
    if (this.userForm.valid) {
      this.isLoading.set(true);
      this.errorMessage.set(null);

      const formValue = this.userForm.value;
      const userData: User = {
        email: formValue.email.trim(),
        firstName: formValue.firstName?.trim() || undefined,
        lastName: formValue.lastName?.trim() || undefined,
        role: formValue.role,
        status: formValue.status,
        // Marketing fields
        phone: formValue.phone?.trim() || undefined,
        company: formValue.company?.trim() || undefined,
        jobTitle: formValue.jobTitle?.trim() || undefined,
        optInMarketing: formValue.optInMarketing !== undefined ? formValue.optInMarketing : true
      };

      // Ajouter le mot de passe seulement s'il est fourni
      if (formValue.password && formValue.password.trim()) {
        userData.password = formValue.password;
      }

      const operation = this.isEditMode() && this.userId()
        ? this.userService.updateUser(this.userId()!, userData)
        : this.userService.createUser(userData);

      operation.subscribe({
        next: () => {
          this.isLoading.set(false);
          this.router.navigate(['/users']);
        },
        error: (error) => {
          this.isLoading.set(false);
          this.errorMessage.set(
            error.error?.error || 'Erreur lors de la sauvegarde de l\'utilisateur'
          );
          console.error('Error saving user:', error);
        }
      });
    } else {
      this.markFormGroupTouched(this.userForm);
    }
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }
}
