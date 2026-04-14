import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Topic } from '../models/topics/topic.model';

@Injectable({ providedIn: 'root' })
export class TopicService {
  private apiUrl = 'http://localhost:8081/teachsync/topics';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt_token');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  getAll(): Observable<Topic[]> {
    return this.http.get<Topic[]>(`${this.apiUrl}/all`, { headers: this.getHeaders() });
  }
}