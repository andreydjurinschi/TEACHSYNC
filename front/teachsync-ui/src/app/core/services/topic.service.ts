import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Topic, TopicTag } from '../models/topics/topic.model';

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

  getById(id: number): Observable<Topic> {
    return this.http.get<Topic>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  getByTag(tag: TopicTag): Observable<Topic[]> {
    return this.http.get<Topic[]>(`${this.apiUrl}/all/by-tag/${tag}`, { headers: this.getHeaders() });
  }

  setTag(topicId: number, tag: TopicTag): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${topicId}/tag/${tag}`, {}, { headers: this.getHeaders() });
  }
}