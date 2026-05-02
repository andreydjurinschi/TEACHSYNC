import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, NgZone } from '@angular/core';
import { Subject } from 'rxjs';
import { NotificationItem, NotificationPreference } from '../models/notifications/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private api = 'http://localhost:8080/teachsync/notifications';
    private refreshSubject = new Subject<void>();
      private realtimeSubject = new Subject<NotificationItem>();
        private eventSource: EventSource | null = null;

          refresh$ = this.refreshSubject.asObservable();
            realtime$ = this.realtimeSubject.asObservable();

              constructor(private http: HttpClient, private zone: NgZone) {}

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

                                                                            getPreferences(userId: number) {
                                                                                return this.http.get<NotificationPreference>(`${this.api}/preferences/${userId}`, { headers: this.getHeaders() });
                                                                                  }

                                                                                    updatePreferences(userId: number, preferences: NotificationPreference) {
                                                                                        return this.http.put<NotificationPreference>(`${this.api}/preferences/${userId}`, preferences, { headers: this.getHeaders() });
                                                                                          }

                                                                                            connectRealtime(userId: number, role: string): void {
                                                                                                if (this.eventSource) {
                                                                                                      return;
                                                                                                          }

                                                                                                              this.eventSource = new EventSource(`${this.api}/stream?userId=${userId}&role=${role}`);
                                                                                                                  this.eventSource.addEventListener('notification', event => {
                                                                                                                        this.zone.run(() => {
                                                                                                                                const notification = JSON.parse((event as MessageEvent).data) as NotificationItem;
                                                                                                                                        this.realtimeSubject.next(notification);
                                                                                                                                                this.triggerRefresh();
                                                                                                                                                      });
                                                                                                                                                          });
                                                                                                                                                            }

                                                                                                                                                              disconnectRealtime(): void {
                                                                                                                                                                  this.eventSource?.close();
                                                                                                                                                                      this.eventSource = null;
                                                                                                                                                                        }

                                                                                                                                                                          triggerRefresh(): void {
                                                                                                                                                                              this.refreshSubject.next();
                                                                                                                                                                                }
                                                                                                                                                                                }
                                                                                                                                                                                