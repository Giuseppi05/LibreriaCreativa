import { Order } from './order';

export interface OrderStatus {
  id: number;
  name: string;
  createdAt: string;
  updatedAt: string;
  orders?: Order[];
}