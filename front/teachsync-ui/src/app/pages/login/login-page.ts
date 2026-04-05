import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login-page.html',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
})
export class LoginComponent {
  form: FormGroup;
  serverError = '';
  loading = false;
  showPassword = false;
  theme: 'dark' | 'light' = 'dark';

  constructor(
    private auth: AuthService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

get emailInvalid() {
  return this.form.controls['email'].invalid && this.form.controls['email'].touched;
}

get passwordInvalid() {
  return this.form.controls['password'].invalid && this.form.controls['password'].touched;
}

  toggleTheme() {
    this.theme = this.theme === 'dark' ? 'light' : 'dark';
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.serverError = '';
    const { email, password } = this.form.value;

    this.auth.login(email, password).subscribe({
      next: () => this.router.navigate(['/users']),
      error: () => {
        this.loading = false;
        this.serverError = 'Неверный email или пароль. Попробуйте снова.';
      },
      complete: () => { this.loading = false; }
    });
  }
}