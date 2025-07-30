import { Order } from '../order';

export interface PageOrder {
  content: Order[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}