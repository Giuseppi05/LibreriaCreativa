export interface Coupon {
  id: number;
  codigoCupon: string;
  descuento: number;
  activo: boolean;
  createdAt?: string;
  updatedAt?: string;
}