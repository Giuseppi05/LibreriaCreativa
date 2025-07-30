import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Subscription, combineLatest, first } from 'rxjs';
import { LoadingService } from '../../services/loading-service';
import { CartService } from '../../services/cart-service';
import { ToastrService } from 'ngx-toastr';
import { CartItem } from '../../models/cart-item';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../services/order-service';

@Component({
  selector: 'app-cart-page',
  imports: [RouterLink, CommonModule],
  templateUrl: './cart-page.html',
  styleUrl: './cart-page.css',
})
export class CartPage implements OnInit, OnDestroy {
  private subscriptions = new Subscription();

  cart: CartItem[] = [];
  readonly SHIPPING_COST = 9.9;
  subtotal = 0;
  total = 0;

  constructor(
    private router: Router,
    private cartService: CartService,
    private toastr: ToastrService,
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
    this.total =
      this.subtotal + (this.cart.length > 0 ? this.SHIPPING_COST : 0);
  }

  removeProduct(id: number): void {
    this.cartService.removeFromCart(id);
    this.cart = this.cartService.getCart();
    this.calculateTotals();

    this.toastr.info('Producto eliminado del carrito', 'Info');
  }

  checkout(): void {
    this.router.navigate(["/payment"]);
    
    // const subtotal = this.subtotal;
    // const total = this.total;

    // const pedidoRequest = {
    //   subtotal,
    //   envio: this.SHIPPING_COST,
    //   total,
    //   productos: this.cart.map((item) => ({
    //     idProducto: item.id,
    //     cantidad: item.quantity,
    //     subtotal: item.quantity * item.price,
    //   })),
    // };

    // this.loadingService.show();

    // this.orderService.saveOrder(pedidoRequest).subscribe({
    //   next: (response) => {
    //     this.loadingService.hide();
    //     this.cartService.clearCart();
    //     this.toastr.success('Compra registrada correctamente', '¡Éxito!');
    //     this.router.navigate(['/orders']);
    //     if (response?.boletaUrl) {
    //       window.open(response.boletaUrl, '_blank');
    //     }
    //   },
    //   error: (error) => {
    //     this.loadingService.hide();
    //     console.error(error);
    //     const msg = error?.error?.error || 'Error al finalizar la compra';
    //     this.toastr.error(msg, 'Error');
    //   },
    // });
  }
}
