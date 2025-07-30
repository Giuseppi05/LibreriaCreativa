import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ProductPage } from '../models/PageModels/page-products';
import { CatalogFilters } from '../models/PageModels/catalog-filters';
import { Product } from '../models/product';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private readonly API_URL = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) {}

  obtenerProductosPaginados(
    page: number = 0,
    size: number = 8,
    sort: string = 'default',
    q?: string,
    filters?: CatalogFilters
  ): Observable<ProductPage> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    if (q) {
      params = params.set('q', q);
    }

    if (filters) {
      if (filters.precio) {
        params = params.set('precio', filters.precio);
      }
      if (filters.stock) {
        params = params.set('stock', filters.stock);
      }
      if (filters.categorias && filters.categorias.length) {
        filters.categorias.forEach((cat) => {
          params = params.append('categorias', cat);
        });
      }
    }

    return this.http.get<ProductPage>(`${this.API_URL}/catalogo`, { params });
  }

  obtenerProductosAdmin(
  page: number = 0,
  size: number = 5
): Observable<ProductPage> {
  const params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString());
  
  return this.http.get<ProductPage>(`${this.API_URL}/admin`, { params });
}

  obtenerProductoPorId(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.API_URL}/product/${id}`);
  }

  obtenerDestacados(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.API_URL}/dest`);
  }

  guardarProducto(formData: FormData): Observable<any> {
    return this.http.post(`${this.API_URL}/save`, formData);
  }

  eliminarProducto(id: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/delete/${id}`);
  }
}
