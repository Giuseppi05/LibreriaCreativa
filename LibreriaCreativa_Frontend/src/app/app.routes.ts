import { AdminCategories } from './pages/adminPages/admin-categories/admin-categories';
import { AdminMessages } from './pages/adminPages/admin-messages/admin-messages';
import { AdminProducts } from './pages/adminPages/admin-products/admin-products';
import { AdminOrders } from './pages/adminPages/admin-orders/admin-orders';
import { ProductPage } from './pages/product-page/product-page';
import { nonAdminGuard } from './guards/non-admin-guard';
import { MyOrders } from './pages/my-orders/my-orders';
import { CartPage } from './pages/cart-page/cart-page';
import { Contact } from './pages/contact/contact';
import { Catalog } from './pages/catalog/catalog';
import { adminGuard } from './guards/admin-guard';
import { authGuard } from './guards/auth-guard';
import { Error } from './pages/error/error';
import { Auth } from './pages/auth/auth';
import { Home } from './pages/home/home';
import { Routes } from '@angular/router';
import { Payment } from './pages/payment/payment';
import { AdminCoupon } from './pages/adminPages/admin-coupon/admin-coupon';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },

  // Rutas públicas protegidas por nonAdminGuard
  { path: 'home', component: Home, canActivate: [nonAdminGuard] },
  { path: 'contact', component: Contact, canActivate: [nonAdminGuard] },
  { path: 'auth', component: Auth, canActivate: [nonAdminGuard] },
  { path: 'catalog', component: Catalog, canActivate: [nonAdminGuard] },
  { path: 'product/:id', component: ProductPage, canActivate: [nonAdminGuard] },
  { path: 'cart', component: CartPage, canActivate: [nonAdminGuard] },
  { path: 'payment', component: Payment, canActivate: [nonAdminGuard, authGuard] },
  { path: 'orders', component: MyOrders, canActivate: [nonAdminGuard, authGuard] },

  // Rutas de admin protegidas por adminGuard
  { path: 'admin/products', component: AdminProducts, canActivate: [adminGuard]},
  { path: 'admin/categories', component: AdminCategories, canActivate: [adminGuard]},
  { path: 'admin/messages', component: AdminMessages, canActivate: [adminGuard]},
  { path: 'admin/orders', component: AdminOrders, canActivate: [adminGuard]},
  { path: 'admin/coupon', component: AdminCoupon, canActivate: [adminGuard]},

  { path: 'error/:code', component: Error },
  { path: '**', redirectTo: 'error/404' },
];
