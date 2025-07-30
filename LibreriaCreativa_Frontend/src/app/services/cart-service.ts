import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { CartItem } from '../models/cart-item';

@Injectable({ providedIn: 'root' })
export class CartService {
  private cartKey = 'cart';
  private cart: CartItem[] = [];
  private cartSubject = new BehaviorSubject<CartItem[]>([]);
  
  cart$ = this.cartSubject.asObservable();

  constructor() {
    this.loadCart();
  }

  private loadCart() {
    const data = localStorage.getItem(this.cartKey);
    this.cart = data ? JSON.parse(data) : [];
    this.cartSubject.next(this.cart);
  }

  private saveCart() {
    localStorage.setItem(this.cartKey, JSON.stringify(this.cart));
    this.cartSubject.next(this.cart);
  }

  getCart(): CartItem[] {
    return [...this.cart];
  }

  getItemCount(): number {
    return this.cart.reduce((sum, item) => sum + item.quantity, 0);
  }

  addToCart(item: CartItem, quantityToAdd: number): string | null {
    const index = this.cart.findIndex(p => p.id === item.id);
    const inCart = index !== -1 ? this.cart[index].quantity : 0;
    const totalAfterAdd = inCart + quantityToAdd;

    if (totalAfterAdd > item.stock) {
      return `Stock insuficiente. Ya tienes ${inCart} en el carrito y el máximo es ${item.stock}.`;
    }

    if (index !== -1) {
      this.cart[index].quantity = totalAfterAdd;
    } else {
      this.cart.push({ ...item, quantity: quantityToAdd });
    }

    this.saveCart();
    return null;
  }

  removeFromCart(itemId: number) {
    this.cart = this.cart.filter(item => item.id !== itemId);
    this.saveCart();
  }

  clearCart() {
    this.cart = [];
    this.saveCart();
  }
}
