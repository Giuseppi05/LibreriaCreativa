import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OrderStatus } from '../models/order-status';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OrderStatusService {
 private readonly API_URL = `${environment.apiUrl}/orderstatus`;

  constructor(private http: HttpClient) {}

  obtenerCategorias(): Observable<OrderStatus[]> {
    return this.http.get<OrderStatus[]>(`${this.API_URL}/all`);
  }
}
