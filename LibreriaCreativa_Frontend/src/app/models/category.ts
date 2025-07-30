import { Product } from './product';

export interface Category {
  id: number;
  name: string;
  createdAt: string;
  updatedAt: string;
  products?: Product[];
}
