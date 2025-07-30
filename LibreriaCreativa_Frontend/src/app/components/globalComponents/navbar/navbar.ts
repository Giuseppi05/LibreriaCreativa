import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth-service';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { LoadingService } from '../../../services/loading-service';
import { CartItem } from '../../../models/cart-item';
import { CartService } from '../../../services/cart-service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit, OnDestroy {
  cartItems: CartItem[] = [];
  cartItemCount = 0;
  isAuthenticated = false;
  isAdmin = false;
  cartUniqueCount = 0;

  private subscriptions = new Subscription();

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService,
    private loadingService: LoadingService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    this.subscriptions.add(
      this.authService.isAuthenticated$.subscribe(
        (value) => (this.isAuthenticated = value)
      )
    );

    this.subscriptions.add(
      this.authService.isAdmin$.subscribe((value) => (this.isAdmin = value))
    );

    this.loadCart();

    this.cartService.cart$.subscribe(() => {
      this.loadCart();
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  logout(): void {
    this.loadingService.show();

    this.authService.logout().subscribe({
      next: () => {
        setTimeout(() => {
          this.loadingService.hide();
          this.toastr.success('Sesión finalizada con éxito', '¡Hasta luego!');
          this.router.navigate(['/auth']);
        }, 300);
      },
      error: () => {
        this.loadingService.hide();
        this.toastr.error('No se pudo cerrar sesión correctamente', 'Ups...');
      },
    });
  }

  private loadCart() {
    const cart = this.cartService.getCart();
    this.cartItemCount = cart.reduce((sum, item) => sum + item.quantity, 0);
    this.cartUniqueCount = cart.length;
    this.cartItems = [...cart].slice(-3).reverse();
  }

  removeFromCartPreview(id: number) {
    this.cartService.removeFromCart(id);
    this.toastr.info("Se removió un producto del carrito", "Producto Removido")
  }
}
