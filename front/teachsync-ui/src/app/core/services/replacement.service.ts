import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ReplacementCreate, ReplacementRequest } from '../models/replacements/replacement.model';

@Injectable({ providedIn: 'root' })
export class ReplacementService {
  private api = 'http://localhost:8080/teachsync/replacements';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt_token');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  create(dto: ReplacementCreate) {
    return this.http.post<ReplacementRequest>(this.api, dto, { headers: this.getHeaders() });
  }

  approve(requestId: number, teacherId: number) {
    return this.http.put<ReplacementRequest>(`${this.api}/${requestId}/approve/${teacherId}`, {}, { headers: this.getHeaders() });
  }

  getForTeacher(teacherId: number) {
    return this.http.get<ReplacementRequest[]>(`${this.api}/teacher/${teacherId}`, { headers: this.getHeaders() });
  }
}
