export interface ProductoItemRequest {
  idProducto: number;
  cantidad: number;
  subtotal: number;
}

export interface DatosTarjetaRequest {
  numero: string;
  cvv: string;
  expiracion: string;
}

export interface PedidoRequest {
  subtotal: number;
  envio: number;
  total: number;
  tipoPago: 'yape' | 'tarjeta' | 'contra-entrega';
  recojoEnTienda: boolean;
  direccionEnvio: string | null;
  productos: ProductoItemRequest[];
  datosTarjeta?: DatosTarjetaRequest | null;
}