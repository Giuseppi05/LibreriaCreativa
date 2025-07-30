import { Category } from '../category';

export interface PageCategories {
  content: Category[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}