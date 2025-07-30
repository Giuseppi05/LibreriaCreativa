import { LoadingService } from '../../../services/loading-service';
import { AuthService } from '../../../services/auth-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs/operators';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  form = {
    email: '',
    password: '',
  };

  errorMessage = '';
  successMessage = '';

  constructor(
    private authService: AuthService,
    private loadingService: LoadingService,
    private toastr: ToastrService,
    private router: Router
  ) {}

  onSubmit() {
    this.errorMessage = '';
    this.successMessage = '';
    this.loadingService.show();

    this.authService
      .login(this.form.email, this.form.password)
      .pipe(finalize(() => this.loadingService.hide()))
      .subscribe({
        next: () => {
          this.toastr.success('Inicio de sesión exitoso', '¡Bienvenido de nuevo!');
          this.successMessage = 'Inicio de sesión exitoso';
          this.router.navigate(['/home']);
        },
        error: (err) => {
          const message =
            err?.error?.message || err?.error?.error || 'Credenciales incorrectas';
          this.errorMessage = message;
          this.toastr.error(message, 'Ups...');
        },
      });
  }

  togglePassword(field: 'password') {
    const input = document.getElementById(field) as HTMLInputElement;
    if (input) {
      input.type = input.type === 'password' ? 'text' : 'password';
    }
  }
}
