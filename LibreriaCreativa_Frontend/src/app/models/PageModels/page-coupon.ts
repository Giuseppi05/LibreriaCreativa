import { Coupon } from '../coupon';

export interface PageCoupon {
  content: Coupon[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}