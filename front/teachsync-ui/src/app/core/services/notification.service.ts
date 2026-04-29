import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { NotificationItem } from '../models/notifications/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private api = 'http://localhost:8080/teachsync/notifications';
  private refreshSubject = new Subject<void>();

  refresh$ = this.refreshSubject.asObservable();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt_token');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  getForRole(role: string, userId: number) {
    return this.http.get<NotificationItem[]>(`${this.api}/role/${role}?userId=${userId}`, { headers: this.getHeaders() });
  }

  getForUser(userId: number) {
    return this.http.get<NotificationItem[]>(`${this.api}/user/${userId}`, { headers: this.getHeaders() });
  }

  getUnreadCountForRole(role: string, userId: number) {
    return this.http.get<number>(`${this.api}/role/${role}/unread-count?userId=${userId}`, { headers: this.getHeaders() });
  }

  getUnreadCountForUser(userId: number) {
    return this.http.get<number>(`${this.api}/user/${userId}/unread-count`, { headers: this.getHeaders() });
  }

  markAsRead(id: number, userId: number) {
    return this.http.patch<void>(`${this.api}/${id}/read?userId=${userId}`, {}, { headers: this.getHeaders() });
  }

  markAllReadForRole(role: string, userId: number) {
    return this.http.patch<void>(`${this.api}/role/${role}/read-all?userId=${userId}`, {}, { headers: this.getHeaders() });
  }

  triggerRefresh(): void {
    this.refreshSubject.next();
  }
}
