import { Component } from '@angular/core';
import { LoadingService } from '../../../services/loading-service';
import { OrderService } from '../../../services/order-service';
import { finalize } from 'rxjs';
import { PageOrder } from '../../../models/PageModels/page-order';
import { CommonModule } from '@angular/common';
import { ModalService } from '../../../services/modal-service';
import { Order } from '../../../models/order';
import { ModalComponent } from '../../../components/globalComponents/modal-component/modal-component';
import { OrderDetail } from '../../../components/adminComponents/order-detail/order-detail';
import { StatusChange } from '../../../components/adminComponents/status-change/status-change';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-admin-orders',
  imports: [CommonModule, ModalComponent, OrderDetail, StatusChange],
  templateUrl: './admin-orders.html',
  styleUrl: './admin-orders.css',
})
export class AdminOrders {
  size = 10;
  page?: PageOrder;
  error?: string;
  modalView: 'detail' | 'status' = 'detail';

  selectedOrder: Order | null = null;

  constructor(
    private loadingService: LoadingService,
    private orderService: OrderService,
    public modalService: ModalService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.cargar(0);
  }

  cargar(page: number = 0) {
    this.loadingService.show();

    this.orderService
      .getAdminOrders(page, this.size)
      .pipe(finalize(() => this.loadingService.hide()))
      .subscribe({
        next: (data) => {
          this.page = data;
          this.error = undefined;
        },
        error: () => {
          this.error = 'Error al cargar los pedidos.';
        },
      });
  }

  totalPagesArray(): number[] {
    return this.page
      ? Array.from({ length: this.page.totalPages }, (_, i) => i)
      : [];
  }

  openDetailModal(order: Order) {
    this.modalView = 'detail';
    this.selectedOrder = order;
    this.modalService.open();
  }

  openStatusModal(order: Order) {
    this.modalView = 'status';
    this.selectedOrder = order;
    this.modalService.open();
  }

  closeModal() {
    this.selectedOrder = null;
    this.modalService.close();
  }

  cambiarEstado(data: { orderId: number; nuevoEstadoId: number }) {
    this.loadingService.show();
    this.orderService
      .updateOrderStatus(data.orderId, data.nuevoEstadoId)
      .pipe(finalize(() => {
        this.loadingService.hide() 
        this.closeModal();}))
      .subscribe({
        next: () => {
          this.closeModal();
          this.toastr.success('Estado actualizado correctamente', 'Completado');
          this.cargar(this.page?.number || 0);
        },
        error: (err) => {
          this.error = err.error.error || 'No se pudo cambiar el estado.';
        },
      });
  }
}
