import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription, finalize } from 'rxjs';

import { CartService } from '../../services/cart-service';
import { LoadingService } from '../../services/loading-service';
import { OrderService } from '../../services/order-service';
import { CouponService } from '../../services/coupon-service';
import { ToastrService } from 'ngx-toastr';

import { CartItem } from '../../models/cart-item';

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment.html',
  styleUrl: './payment.css',
})
export class Payment implements OnInit, OnDestroy {
  private subscriptions = new Subscription();

  // Carrito
  cart: CartItem[] = [];
  shippingCost = 9.9;
  subtotal = 0;
  discount = 0;
  total = 0;

  // Cupón
  couponCode = '';
  couponApplied = false;
  couponError = '';

  // Pago
  paymentType = '';
  isPickup = false;
  address = '';
  selectedFile: File | null = null;

  // Datos tarjeta
  cardNumber = '';
  cardCVV = '';
  cardExpiry = '';

  // Errores de formulario
  paymentErrors = {
    paymentType: '',
    address: '',
    selectedFile: '',
    cardNumber: '',
    cardCVV: '',
    cardExpiry: '',
  };

  constructor(
    private router: Router,
    private cartService: CartService,
    private loadingService: LoadingService,
    private toastr: ToastrService,
    private orderService: OrderService,
    private couponService: CouponService
  ) {}

  ngOnInit(): void {
    this.subscriptions.add(
      this.cartService.cart$.subscribe((cart) => {
        this.cart = cart;
        this.calculateTotals();
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  calculateTotals(): void {
    this.subtotal = this.cart.reduce(
      (sum, item) => sum + item.price * item.quantity,
      0
    );

    const envio = this.cart.length > 0 ? this.shippingCost : 0;
    this.total = this.subtotal - this.discount + envio;
  }

  applyCoupon(): void {
    this.couponError = '';
    const code = this.couponCode.trim().toUpperCase();

    if (!code) {
      this.couponError = 'Por favor ingresa un código de cupón.';
      return;
    }

    this.loadingService.show();
    this.couponService
      .obtenerCuponPorCodigo(code)
      .pipe(
        finalize(() => {
          this.loadingService.hide();
          this.calculateTotals();
        })
      )
      .subscribe({
        next: (res) => {
          this.discount = (this.subtotal * res.descuento) / 100;
          this.couponApplied = true;
          this.toastr.success('Cupón aplicado con éxito', '¡Descuento!');
        },
        error: (err) => {
          this.discount = 0;
          this.couponApplied = false;
          this.couponError = err?.error || 'No se pudo aplicar el cupón.';
        },
      });
  }

  removeCoupon(): void {
    this.couponCode = '';
    this.discount = 0;
    this.couponApplied = false;
    this.couponError = '';
    this.calculateTotals();
  }

  onPaymentTypeChange(): void {
    this.isPickup = false;
    this.address = '';
    this.clearPaymentErrors();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  onPickupToggle(checked: boolean): void {
    this.shippingCost = checked ? 0 : 9.9;
    this.calculateTotals();
  }

  private clearPaymentErrors(): void {
    this.paymentErrors = {
      paymentType: '',
      address: '',
      selectedFile: '',
      cardNumber: '',
      cardCVV: '',
      cardExpiry: '',
    };
  }

  processPayment(): void {
    this.clearPaymentErrors();

    // Validar tipo de pago
    if (!this.paymentType) {
      this.paymentErrors.paymentType = 'Selecciona un método de pago.';
      return;
    }

    // Validar dirección si no es recojo
    if (!this.isPickup && !this.address.trim()) {
      this.paymentErrors.address = 'Ingresa tu dirección de envío.';
      return;
    }

    // Validar Yape
    if (this.paymentType === 'yape' && !this.selectedFile) {
      this.paymentErrors.selectedFile =
        'Debes subir el comprobante de pago (imagen).';
      return;
    }

    // Validar tarjeta
    if (this.paymentType === 'tarjeta') {
      if (!this.isValidCardNumber(this.cardNumber)) {
        this.paymentErrors.cardNumber = 'Número de tarjeta inválido.';
        return;
      }
      if (!this.cardCVV.trim() || !/^\d{3,4}$/.test(this.cardCVV)) {
        this.paymentErrors.cardCVV = 'CVV inválido.';
        return;
      }
      if (!this.cardExpiry.trim() || !this.isValidExpiryDate(this.cardExpiry)) {
        this.paymentErrors.cardExpiry =
          'Fecha de expiración inválida o vencida (MM/AA).';
        return;
      }
    }

    const envio = this.isPickup ? 0 : this.shippingCost;

    // Armar objeto pedido
    const pedidoRequest: any = {
      subtotal: this.subtotal,
      descuento: this.discount,
      envio,
      total: this.total,
      tipoPago: this.paymentType,
      recojoEnTienda: this.isPickup,
      direccion: this.isPickup ? null : this.address,
      productos: this.cart.map((item) => ({
        idProducto: item.id,
        cantidad: item.quantity,
        subtotal: item.quantity * item.price,
      })),
      datosTarjeta:
        this.paymentType === 'tarjeta'
          ? {
              numero: this.cardNumber,
              cvv: this.cardCVV,
              expiracion: this.cardExpiry,
            }
          : null,
    };

    // Armar FormData SIEMPRE
    const formData = new FormData();
    formData.append(
      'pedido',
      new Blob([JSON.stringify(pedidoRequest)], { type: 'application/json' })
    );

    if (this.selectedFile) {
      formData.append('comprobante', this.selectedFile);
    }

    // Enviar
    this.sendOrder(formData);
  }

  private sendOrder(payload: any): void {
    this.loadingService.show();

    this.orderService.saveOrder(payload).subscribe({
      next: (response) => {
        this.loadingService.hide();
        this.cartService.clearCart();
        this.toastr.success('Compra registrada correctamente', '¡Éxito!');
        this.router.navigate(['/orders']);
      },
      error: (error) => {
        this.loadingService.hide();
        console.error(error);
        const msg = error?.error?.error || 'Error al finalizar la compra';
        this.toastr.error(msg, 'Error');
      },
    });
  }

  private isValidExpiryDate(expiry: string): boolean {
    if (!expiry) return false;

    const match = expiry.match(/^(\d{2})\/(\d{2})$/);
    if (!match) return false;

    const [_, mmStr, yyStr] = match;
    const month = parseInt(mmStr, 10);
    const year = parseInt(yyStr, 10) + 2000;

    if (month < 1 || month > 12) return false;

    const now = new Date();
    const expDate = new Date(year, month);

    return expDate > now;
  }

  private isValidCardNumber(cardNumber: string): boolean {
    if (!cardNumber) return false;

    const sanitized = cardNumber.replace(/\s+/g, '');
    if (sanitized.length !== 16) return false;
    if (!/^\d+$/.test(sanitized)) return false;

    let sum = 0;
    let shouldDouble = false;

    for (let i = sanitized.length - 1; i >= 0; i--) {
      let digit = parseInt(sanitized.charAt(i), 10);
      if (shouldDouble) {
        digit *= 2;
        if (digit > 9) digit -= 9;
      }
      sum += digit;
      shouldDouble = !shouldDouble;
    }
    return sum % 10 === 0;
  }
}
