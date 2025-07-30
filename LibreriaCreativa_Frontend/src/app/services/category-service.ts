import { Injectable } from '@angular/core';
import { Category } from '../models/category';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { PageCategories } from '../models/PageModels/page-categories';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private readonly API_URL = 'http://localhost:8080/api/category';

  constructor(private http: HttpClient) {}

  obtenerCategorias(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.API_URL}/all`);
  }

  getCategoriesAdmin(
    page: number = 0,
    size: number = 0
  ): Observable<PageCategories> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageCategories>(`${this.API_URL}/admin`, { params });
  }

  saveCategory(c: Category): Observable<any> {
    return this.http.post(`${this.API_URL}/save`, c);
  }

  updateCategory(c: Category): Observable<any> {
    return this.http.put(`${this.API_URL}/update`, c)
  }

  deleteCategory(id: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/delete/${id}`);
  }
}
