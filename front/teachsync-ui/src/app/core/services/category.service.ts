import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CategoryBase } from '../models/category/category.model';

@Injectable({ providedIn: 'root' })
export class CategoryService {
  
  private base = 'http://localhost:8080/teachsync/categories';
  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt_token');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  getAll(): Observable<CategoryBase[]> {
    return this.http.get<CategoryBase[]>(`${this.base}/all`, { headers: this.getHeaders() });
  }

  create(dto: { name: string }): Observable<void> {
    return this.http.post<void>(`${this.base}/create`, dto, { headers: this.getHeaders() });
  }

  update(id: number, dto: { name: string }): Observable<void> {
    return this.http.put<void>(`${this.base}/update/${id}`, dto, { headers: this.getHeaders() });
  }
}
