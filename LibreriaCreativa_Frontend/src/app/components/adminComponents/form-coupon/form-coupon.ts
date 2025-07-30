import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Coupon } from '../../../models/coupon';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoadingService } from '../../../services/loading-service';
import { CouponService } from '../../../services/coupon-service';
import { finalize } from 'rxjs';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-form-coupons',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './form-coupon.html',
  styleUrl: './form-coupon.css',
})
export class FormCoupons {
  @Input() initialCoupon?: Coupon;
  @Output() saved = new EventEmitter<'add' | 'edit'>();

  form: FormGroup;
  error?: string;
  isEditMode = false;

  constructor(
    private fb: FormBuilder,
    private loadingService: LoadingService,
    private couponService: CouponService
  ) {
    this.form = this.fb.group({
      id: [],
      codigoCupon: ['', Validators.required],
      descuento: [
        0,
        [Validators.required, Validators.min(0), Validators.max(100)],
      ],
      activo: [true],
    });
  }

  ngOnInit() {
    if (this.initialCoupon) {
      this.isEditMode = true;
      this.patchForm(this.initialCoupon);
    }
  }

  patchForm(c: Coupon): void {
    this.form.patchValue({
      id: c.id,
      codigoCupon: c.codigoCupon,
      descuento: c.descuento,
      activo: c.activo,
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.error = 'Completa todos los campos obligatorios.';
      this.form.markAllAsTouched();
      return;
    }

    this.error = undefined;
    this.loadingService.show();

    const couponData: Coupon = this.form.value;

    const request$ = this.isEditMode
      ? this.couponService.updateCoupon(couponData)
      : this.couponService.saveCoupon(couponData);

    request$.pipe(finalize(() => this.loadingService.hide())).subscribe({
      next: () => this.saved.emit(this.isEditMode ? 'edit' : 'add'),
      error: (err) =>
        (this.error = err.error?.error || 'Error al guardar el cupón.'),
    });
  }

  controlInvalid(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}
