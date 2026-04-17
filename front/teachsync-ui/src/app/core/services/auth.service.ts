import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import {
  BehaviorSubject,
  Observable,
} from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8090/teachsync/auth';
  private tokenKey = 'jwt_token';

  private userRoleSubject = new BehaviorSubject<string | null>(null);
  userRole$ = this.userRoleSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<any> {
    return this.http.post<{token: string}>(`${this.apiUrl}/login`, {email, password})
      .pipe(
        tap(res => {
          localStorage.setItem(this.tokenKey, res.token);
          const payload = this.decodeToken(res.token);
          this.userRoleSubject.next(payload.role);
        })
      );
  }

  logout() {
    //localStorage.removeItem(this.tokenKey);
    localStorage.clear();
    this.userRoleSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getRole(): string | null {
    return this.userRoleSubject.value;
  }

  private decodeToken(token: string): any {
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload));
    } catch {
      return null;
    }
  }
}
