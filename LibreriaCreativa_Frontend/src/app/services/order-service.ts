import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { PedidoRequest } from '../models/pedido-request';
import { Observable } from 'rxjs';
import { PageOrder } from '../models/PageModels/page-order';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly API_URL = 'http://localhost:8080/api/order';

  constructor(private http: HttpClient) {}

  saveOrder(request: any): Observable<any> {
    return this.http.post(`${this.API_URL}/save`, request);
  }

  cancelOrder(orderId: number): Observable<any> {
    return this.http.put(`${this.API_URL}/cancelarUser/${orderId}`, null);
  }

  updateOrderStatus(orderId: number, nuevoEstadoId: number): Observable<any> {
    const params = new HttpParams().set(
      'nuevoEstadoId',
      nuevoEstadoId.toString()
    );
    return this.http.put(`${this.API_URL}/status/${orderId}`, null, { params });
  }

  getAdminOrders(page: number = 0, size: number = 10): Observable<PageOrder> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageOrder>(`${this.API_URL}/admin`, { params });
  }

  getMineOrders(page: number = 0, size: number = 10): Observable<PageOrder> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageOrder>(`${this.API_URL}/mine`, { params });
  }
}
