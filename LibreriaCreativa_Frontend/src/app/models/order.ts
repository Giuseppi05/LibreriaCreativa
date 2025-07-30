import { User } from './user';
import { OrderDetail } from './order-detail';
import { OrderStatus } from './order-status';
import { Payment } from './payment';

export interface Order {
  id: number;
  user: User;
  subtotal: number;
  envio: number;
  descuento: number;
  total: number;
  direccion: string | null;
  estado: OrderStatus;
  rutaBoleta: string | null;
  createdAt: string;
  updatedAt: string;
  detalles?: OrderDetail[];
  payments?: Payment[];
}
