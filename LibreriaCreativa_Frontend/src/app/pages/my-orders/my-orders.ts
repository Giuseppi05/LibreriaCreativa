import { Component } from '@angular/core';
import { PageOrder } from '../../models/PageModels/page-order';
import { LoadingService } from '../../services/loading-service';
import { OrderService } from '../../services/order-service';
import { finalize } from 'rxjs';
import { CommonModule } from '@angular/common';
import { ModalService } from '../../services/modal-service';
import { ModalComponent } from '../../components/globalComponents/modal-component/modal-component';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-my-orders',
  imports: [CommonModule, ModalComponent],
  templateUrl: './my-orders.html',
  styleUrl: './my-orders.css',
})
export class MyOrders {
  page?: PageOrder;
  error?: undefined;
  size = 5;
  selectedYapeImage: string | null = null;

  constructor(
    private loadingService: LoadingService,
    private orderService: OrderService,
    public modalService: ModalService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.cargar(0);
  }

  cargar(page: number) {
    this.loadingService.show();

    this.orderService
      .getMineOrders(page, this.size)
      .pipe(finalize(() => this.loadingService.hide()))
      .subscribe({
        next: (data) => {
          console.log(data);
          this.page = data;
          this.error = undefined;
        },
        error: (err) => {
          this.error = err.error.error;
        },
      });
  }

  totalPagesArray(): number[] {
    return this.page
      ? Array.from({ length: this.page.totalPages }, (_, i) => i)
      : [];
  }

  openYapeModal(imageUrl: string | undefined) {
    if (imageUrl) {
      this.selectedYapeImage = imageUrl;
      this.modalService.open();
    }
  }

  closeYapeModal() {
    this.selectedYapeImage = null;
    this.modalService.close();
  }

  cancelOrder(orderId: number) {
    const confirmed = window.confirm(
      '¿Estás seguro de que deseas cancelar este pedido? Esta acción no se puede deshacer.'
    );

    if (!confirmed) {
      return;
    }

    this.loadingService.show();

    this.orderService
      .cancelOrder(orderId)
      .pipe(finalize(() => this.loadingService.hide()))
      .subscribe({
        next: (res) => {
          this.toastr.success('Pedido Cancelado Exitosamente', 'Success')
          this.cargar(0);
        },
        error: (err) => {
          console.error(err);
          this.toastr.error(err?.error?.error || 'Error al cancelar el pedido', 'Error')
        },
      });
  }
}
