import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ProductService } from '../../services/product-service';
import { Product } from '../../models/product';
import { LoadingService } from '../../services/loading-service';
import { CartItem } from '../../models/cart-item';
import { CartService } from '../../services/cart-service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-product-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './product-page.html',
  styleUrl: './product-page.css',
})
export class ProductPage {
  product: Product | null = null;
  quantity = 1;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private loadingService: LoadingService,
    private cartService: CartService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      if (id) {
        this.loadProduct(+id);
      }
    });
  }

  loadProduct(id: number) {
    this.loadingService.show();
    this.productService.obtenerProductoPorId(id).subscribe({
      next: (data) => {
        this.product = data;
        this.quantity = 1;
        this.loadingService.hide();
      },
      error: (err) => {
        console.error('Error cargando producto', err);
        this.loadingService.hide();
      },
    });
  }

  increaseQuantity() {
    if (!this.product) return;

    const inCart =
      this.cartService.getCart().find((item) => item.id === this.product!.id)
        ?.quantity || 0;

    const max = this.product.stock;
    const remaining = max - inCart;

    if (this.quantity < remaining) {
      this.quantity++;
    } else {
      if (inCart > 0) {
        this.toastr.warning(
          `Ya tienes ${inCart} en el carrito. Stock disponible: ${max}`,
          'Stock insuficiente'
        );
      } else {
        this.toastr.warning(
          `Stock máximo disponible: ${max}`,
          'Stock insuficiente'
        );
      }
    }
  }

  decreaseQuantity() {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  addToCart() {
    if (!this.product) return;

    const cartItem: CartItem = {
      id: this.product.id,
      name: this.product.name,
      img: this.product.img,
      price: this.product.precio,
      quantity: 0,
      stock: this.product.stock,
    };

    const error = this.cartService.addToCart(cartItem, this.quantity);
    if (error) {
      this.toastr.error(error, 'Error');
    } else {
      this.toastr.success(
        `Se añadió ${this.quantity} unidad(es) del producto "${this.product.name}" al carrito`,
        'Producto añadido'
      );
    }
  }

  buyNow() {
    this.addToCart();
    window.location.href = '/cart';
  }

  isSpecsMapEmpty(specsMap?: { [key: string]: any }): boolean {
    return !specsMap || Object.keys(specsMap).length === 0;
  }
}
