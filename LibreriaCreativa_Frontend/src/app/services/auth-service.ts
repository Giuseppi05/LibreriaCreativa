import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { tap, map, catchError } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API_URL = 'http://localhost:8080';

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  private isAdminSubject = new BehaviorSubject<boolean>(false);

  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  isAdmin$ = this.isAdminSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Login user and store token and admin flag.
   */
  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/api/auth/login`, { email, password }).pipe(
      tap((res) => {
        localStorage.setItem('token', res.token);
        this.isAuthenticatedSubject.next(true);
        this.isAdminSubject.next(!!res.admin);
      })
    );
  }

  /**
   * Register new user.
   */
  register(user: Partial<any>): Observable<{ success: boolean; message?: string }> {
    return this.http.post<{ success: boolean; message?: string }>(
      `${this.API_URL}/api/auth/register`,
      user
    );
  }

  /**
   * Logout user and clear local state.
   */
  logout(): Observable<void> {
    localStorage.removeItem('token');
    this.isAuthenticatedSubject.next(false);
    this.isAdminSubject.next(false);
    return of(undefined);
  }

  /**
   * Return stored JWT token.
   */
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  /**
   * Initialize authentication state on app start.
   * Used with provideAppInitializer to ensure guards see correct values.
   */
  initializeAuth(): Promise<void> {
    return new Promise<void>((resolve) => {
      this.checkStoredTokenObservable().subscribe({
        complete: () => resolve(),
        error: () => resolve()
      });
    });
  }

  /**
   * Verify stored token with backend and set user state.
   */
  private checkStoredTokenObservable(): Observable<void> {
    const token = this.getToken();
    if (!token) {
      this.clearAuth();
      return of(void 0);
    }

    return this.http.get<{ authenticated: boolean; admin: boolean }>(
      `${this.API_URL}/api/auth/status`,
      { headers: { Authorization: `Bearer ${token}` } }
    ).pipe(
      tap((res) => {
        if (res.authenticated) {
          this.isAuthenticatedSubject.next(true);
          this.isAdminSubject.next(res.admin);
        } else {
          this.clearAuth();
        }
      }),
      map(() => void 0),
      catchError((err) => {
        console.error('Auth check failed!', err);
        this.clearAuth();
        return of(void 0);
      })
    );
  }

  /**
   * Clears authentication state and localStorage.
   */
  private clearAuth(): void {
    localStorage.removeItem('token');
    this.isAuthenticatedSubject.next(false);
    this.isAdminSubject.next(false);
  }

  /**
   * Checks if a token is stored locally.
   */
  private hasToken(): boolean {
    return !!localStorage.getItem('token');
  }
}
