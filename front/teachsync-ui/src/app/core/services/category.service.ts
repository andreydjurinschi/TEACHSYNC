import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CategoryBase } from '../models/category/category.model';

@Injectable({ providedIn: 'root' })
export class CategoryService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8081/teachsync/categories';

  getAll(): Observable<CategoryBase[]> {
    return this.http.get<CategoryBase[]>(`${this.base}/all`);
  }

  create(dto: { name: string }): Observable<void> {
    return this.http.post<void>(`${this.base}/create`, dto);
  }

  update(id: number, dto: { name: string }): Observable<void> {
    return this.http.put<void>(`${this.base}/update/${id}`, dto);
  }
}