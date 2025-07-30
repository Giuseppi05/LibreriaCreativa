import { Component } from '@angular/core';
import { PageMessages } from '../../../models/PageModels/page-messages';
import { LoadingService } from '../../../services/loading-service';
import { MessageService } from '../../../services/message-service';
import { finalize } from 'rxjs';
import { ModalComponent } from '../../../components/globalComponents/modal-component/modal-component';
import { ModalService } from '../../../services/modal-service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-messages',
  imports: [ModalComponent, CommonModule],
  templateUrl: './admin-messages.html',
  styleUrl: './admin-messages.css',
})
export class AdminMessages {
  size = 10;
  page?: PageMessages;
  error?: string;
  selectedMessage?: any;

  constructor(
    private loadingService: LoadingService,
    private messageService: MessageService,
    public modalService: ModalService
  ) {}

  ngOnInit() {
    this.cargar(0);
  }

  cargar(page: number = 0) {
    this.loadingService.show();

    this.messageService
      .getMessagesAdmin(page, this.size)
      .pipe(finalize(() => this.loadingService.hide()))
      .subscribe({
        next: (data) => {
          this.page = data;
          this.error = undefined;
        },
        error: () => {
          this.error = 'Error al cargar los mensajes.';
        },
      });
  }

  totalPagesArray(): number[] {
    return this.page
      ? Array.from({ length: this.page.totalPages }, (_, i) => i)
      : [];
  }

  abrirModal(mensaje: any) {
    this.selectedMessage = mensaje;
    this.modalService.open();
  }

  closeModal() {
    this.modalService.close();
  }
}
