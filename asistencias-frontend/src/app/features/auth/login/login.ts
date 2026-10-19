import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/auth/auth.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-login',
  standalone: true,
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly form = this.fb.nonNullable.group({
    username:['', [
      Validators.required,
      Validators.minLength(4),
      Validators.maxLength(50)
    ]],
    password:['', [
      Validators.required,
      Validators.minLength(5),
      Validators.maxLength(100)
    ]]
  });

  isSubmitting = false;
  errorMessage = '';

  submit(): void {
    this.errorMessage = '';

    if(this.form.invalid){
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;

    this.authService.login(this.form.getRawValue()).subscribe({
      next: () => {
        this.isSubmitting = false;
        void this.router.navigateByUrl('/dashboard');
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting = false;

        if(error.status === 401){
          this.errorMessage = 'Usuario o Contraseña incorrectos';
        }else if(error.status ===0) {
          this.errorMessage = 'No se pudo conectar con el servidor. Verifique que la api este funcionando';
        }else {
          this.errorMessage = 'No se pudo iniciar sesión. Intenta nuevamente';
        }
      }
    })
  }
}
