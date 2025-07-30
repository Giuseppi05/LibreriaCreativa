import { Component } from '@angular/core';
import { PageCoupon } from '../../../models/PageModels/page-coupon';
import { Coupon } from '../../../models/coupon';
import { CouponService } from '../../../services/coupon-service';
import { LoadingService } from '../../../services/loading-service';
import { ModalService } from '../../../services/modal-service';
import { finalize } from 'rxjs';
import { CommonModule } from '@angular/common';
import { ModalComponent } from '../../../components/globalComponents/modal-component/modal-component';
import { FormCoupons } from '../../../components/adminComponents/form-coupon/form-coupon';

@Component({
  selector: 'app-admin-coupon',
  imports: [CommonModule, ModalComponent, FormCoupons],
  templateUrl: './admin-coupon.html',
  styleUrl: './admin-coupon.css',
})
export class AdminCoupon {
  page?: PageCoupon;
  size = 10;
  error?: string;
  success?: string;
  couponToEdit?: Coupon;

  constructor(
    private couponService: CouponService,
    private loadingService: LoadingService,
    public modalService: ModalService
  ) {}

  ngOnInit() {
    this.cargar(0);
  }

  cargar(page: number): void {
    this.loadingService.show();

    this.couponService
      .getCouponsAdmin(page, this.size)
      .pipe(finalize(() => this.loadingService.hide()))
      .subscribe({
        next: (data) => {
          this.page = data;
          this.error = undefined;
        },
        error: () => {
          this.error = 'Error al cargar las categorias.';
        },
      });
  }

  borrar(id: number): void {
    if (confirm('¿Seguro que deseas eliminar esta cupón?')) {
      this.loadingService.show();

      this.couponService
        .deleteCoupon(id)
        .pipe(finalize(() => this.loadingService.hide()))
        .subscribe({
          next: () => {
            this.success = 'Cupón eliminado correctamente.';
            this.cargar(this.page?.number || 0);
          },
          error: () => {
            this.error = 'No se pudo eliminar el cupón.';
          },
        });
    }
  }

  totalPagesArray(): number[] {
    return this.page
      ? Array.from({ length: this.page.totalPages }, (_, i) => i)
      : [];
  }

  openModal(coupon?: Coupon) {
    this.couponToEdit = coupon;
    this.modalService.open();
  }

  closeModal() {
    this.modalService.close();
  }

  onCouponSaved(action: 'add' | 'edit'): void {
    this.closeModal();
    this.success =
      action === 'add'
        ? 'Cupón añadido correctamente'
        : 'Cupón editado correctamente';
    this.cargar(this.page?.number || 0);
  }

  editar(coupon: Coupon): void {
    this.openModal(coupon);
  }
}
