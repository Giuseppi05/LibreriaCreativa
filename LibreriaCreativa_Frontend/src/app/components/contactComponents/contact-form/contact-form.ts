import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { LoadingService } from '../../../services/loading-service';
import { MessageService } from '../../../services/message-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-contact-form',
  imports: [CommonModule, FormsModule],
  templateUrl: './contact-form.html',
  styleUrl: './contact-form.css',
})

export class ContactForm {
  form = {
    name: '',
    email: '',
    affair: '',
    message: '',
  };

  successMessage = '';
  errorMessage = '';

  constructor(
    private toastr: ToastrService,
    private loadingService: LoadingService,
    private messageService: MessageService,
    private router: Router
  ) {}

  onSubmit() {
    this.successMessage = '';
    this.errorMessage = '';
    this.loadingService.show();

    this.messageService
      .saveMessage(this.form)
      .pipe(finalize(() => this.loadingService.hide()))
      .subscribe({
        next: (res) => {
          setTimeout(() => {
          this.successMessage = res.message || 'Mensaje enviado con éxito';
          this.toastr.success(this.successMessage, '¡Gracias!');
          this.router.navigate(['/']);
          }, 300);
        },
        error: (err) => {
          const msg =
            err?.error?.message ||
            err?.error?.error ||
            'Error al enviar mensaje';
          this.errorMessage = msg;
          this.toastr.error(msg, 'Ups...');
        },
      });
  }
}