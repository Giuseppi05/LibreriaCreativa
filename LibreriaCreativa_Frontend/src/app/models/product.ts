import { Category } from './category';
import { OrderDetail } from './order-detail';

export interface Product {
  id: number;
  name: string;
  img: string;
  stock: number;
  precio: number;
  marca: string;
  description: string;
  specsMap?: { [key: string]: any };
  specs?: string;

  active: boolean;
  createdAt: string;
  updatedAt: string;

  category?: Category;
  detalles?: OrderDetail[];
}
