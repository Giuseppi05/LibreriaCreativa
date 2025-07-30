import { Component, EventEmitter, Output } from '@angular/core';
import { LoadingService } from '../../../services/loading-service';
import { AuthService } from '../../../services/auth-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs/operators';
import { User } from '../../../models/user';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  form: Partial<User> & { password: string; confirmPassword: string } = {
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
  };

  @Output() registered = new EventEmitter<void>();

  errorMessage = '';
  successMessage = '';

  constructor(
    private authService: AuthService,
    private loadingService: LoadingService,
    private toastr: ToastrService
  ) {}

  onSubmit() {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.form.password !== this.form.confirmPassword) {
      this.toastr.error('Las contraseñas no coinciden', 'Validación');
      return;
    }

    this.loadingService.show();

    this.authService
      .register(this.form)
      .pipe(finalize(() => this.loadingService.hide()))
      .subscribe({
        next: (res) => {
          this.toastr.success('Cuenta creada con éxito', '¡Bienvenido!');
          this.successMessage = res.message || '';
          this.registered.emit();
        },
        error: (err) => {
          const msg =
            err?.error?.message || err?.error?.error || 'Error al registrar usuario';
          this.errorMessage = msg;
          this.toastr.error(msg, 'Ups...');
        },
      });
  }

  togglePassword(field: 'password' | 'confirmPassword') {
    const input = document.getElementById(field) as HTMLInputElement;
    if (input) {
      input.type = input.type === 'password' ? 'text' : 'password';
    }
  }
}