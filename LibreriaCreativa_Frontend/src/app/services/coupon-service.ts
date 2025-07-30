import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Coupon } from '../models/coupon';
import { PageCoupon } from '../models/PageModels/page-coupon';

@Injectable({
  providedIn: 'root',
})
export class CouponService {
  private readonly API_URL = 'http://localhost:8080/api/coupon';

  constructor(private http: HttpClient) {}

  obtenerCuponPorCodigo(code: string): Observable<Coupon> {
    return this.http.get<Coupon>(`${this.API_URL}/coupon/${code}`);
  }

  getCouponsAdmin(page: number = 0, size: number = 0): Observable<PageCoupon> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageCoupon>(`${this.API_URL}/admin`, { params });
  }

  saveCoupon(c: Coupon): Observable<any> {
    return this.http.post(`${this.API_URL}/save`, c);
  }

  updateCoupon(c: Coupon): Observable<any> {
    return this.http.put(`${this.API_URL}/update`, c);
  }

  deleteCoupon(id: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/delete/${id}`);
  }
}
