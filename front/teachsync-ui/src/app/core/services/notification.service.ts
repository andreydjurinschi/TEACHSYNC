import {
  HttpClient,
  HttpHeaders,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  Observable,
  Subject,
} from 'rxjs';

import { NotificationItem } from '../models/notifications/notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = 'http://localhost:8080/teachsync/notifications';
  private refreshSubject = new Subject<void>();
  refresh$ = this.refreshSubject.asObservable();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt_token');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  getForRole(role: string, userId: number): Observable<NotificationItem[]> {
    return this.http.get<NotificationItem[]>(
      `${this.apiUrl}/role/${role}?userId=${userId}`,
      { headers: this.getHeaders() }
    );
  }

  getForUser(userId: number): Observable<NotificationItem[]> {
    return this.http.get<NotificationItem[]>(
      `${this.apiUrl}/user/${userId}`,
      { headers: this.getHeaders() }
    );
  }

  getUnreadCountForRole(role: string, userId: number): Observable<number> {
    return this.http.get<number>(
      `${this.apiUrl}/role/${role}/unread-count?userId=${userId}`,
      { headers: this.getHeaders() }
    );
  }

  getUnreadCountForUser(userId: number): Observable<number> {
    return this.http.get<number>(
      `${this.apiUrl}/user/${userId}/unread-count`,
      { headers: this.getHeaders() }
    );
  }

  markAsRead(notificationId: number, userId: number): Observable<void> {
    return this.http.patch<void>(
      `${this.apiUrl}/${notificationId}/read?userId=${userId}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  markAllReadForRole(role: string, userId: number): Observable<void> {
    return this.http.patch<void>(
      `${this.apiUrl}/role/${role}/read-all?userId=${userId}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  triggerRefresh(): void {
    this.refreshSubject.next();
  }
}
