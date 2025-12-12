import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../core/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  registerForm: FormGroup;
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  isLoading = signal<boolean>(false);

  constructor() {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      firstName: [''],
      lastName: ['']
      // Role is NOT included - it will be set to PARTICIPANT by default on the backend
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isLoading.set(true);
      this.errorMessage.set(null);
      this.successMessage.set(null);

      const formValue = this.registerForm.value;
      // Nettoyer les données - enlever les champs vides
      // Role is NOT sent - backend will assign PARTICIPANT by default
      const registerData: any = {
        email: formValue.email.trim(),
        password: formValue.password
      };
      
      // Ajouter firstName et lastName seulement s'ils ne sont pas vides
      if (formValue.firstName && formValue.firstName.trim()) {
        registerData.firstName = formValue.firstName.trim();
      }
      if (formValue.lastName && formValue.lastName.trim()) {
        registerData.lastName = formValue.lastName.trim();
      }

      console.log('Register data being sent:', registerData);

      this.authService.register(registerData).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.successMessage.set('Compte créé avec succès ! Redirection vers la page de connexion...');
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
        },
        error: (error) => {
          this.isLoading.set(false);
          console.error('Registration error:', error);
          
          // Extraire le message d'erreur
          let errorMsg = 'Erreur lors de la création du compte. Veuillez réessayer.';
          
          if (error.error) {
            if (typeof error.error === 'string') {
              errorMsg = error.error;
            } else if (error.error.error) {
              errorMsg = error.error.error;
            } else if (error.error.message) {
              errorMsg = error.error.message;
            }
          } else if (error.message) {
            errorMsg = error.message;
          }
          
          this.errorMessage.set(errorMsg);
        }
      });
    } else {
      this.markFormGroupTouched(this.registerForm);
    }
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }
}
