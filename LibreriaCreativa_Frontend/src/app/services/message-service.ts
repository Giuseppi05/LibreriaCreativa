import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message } from '../models/message';
import { PageMessages } from '../models/PageModels/page-messages';4
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class MessageService {
  private readonly API_URL = `${environment.apiUrl}/messages`;

  constructor(private http: HttpClient) {}

  saveMessage(
    message: Message
  ): Observable<{ success: boolean; message?: string }> {
    return this.http.post<{ success: boolean; message?: string }>(
      `${this.API_URL}/save`,
      message
    );
  }

  getMessagesAdmin(
    page: number = 0,
    size: number = 0
  ): Observable<PageMessages> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageMessages>(`${this.API_URL}/admin`, { params });
  }
}
