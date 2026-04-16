import {
  HttpClient,
  HttpHeaders,
} from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs/internal/Observable';

import { UserWithCourses } from '../models/users/user.detailed.model';
import { User } from '../models/users/user.model';
import { EditAccount } from '../models/profile/pofile-editor.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/teachsync/users';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt_token');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  getAll(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/all`, { headers: this.getHeaders() });
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  update(id: number, user: Partial<User>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/edit/${id}`, user, { headers: this.getHeaders() });
  }
  
  editAccount(id:number, editedData: Partial<EditAccount>): Observable<void>{
    return this.http.put<void>(`${this.apiUrl}/edit/account/${id}`, editedData, { headers: this.getHeaders() });
  }
  
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`, { headers: this.getHeaders() });
  }

  create(user: Partial<User>): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/create`, user, { headers: this.getHeaders() });
  }

  getWithCourses(id: number): Observable<UserWithCourses> {
    return this.http.get<UserWithCourses>(`${this.apiUrl}/teacher/${id}/courses`, { headers: this.getHeaders() });
  }
}