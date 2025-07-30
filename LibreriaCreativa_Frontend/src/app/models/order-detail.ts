import { Product } from './product';
import { Order } from './order';

export interface OrderDetail {
  pedido: Order;           
  producto: Product;      
  cantidad: number;
  subtotal: number;
  createdAt: string;
  updatedAt: string;
}
