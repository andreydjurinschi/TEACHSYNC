import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GroupBase } from '../models/groups/group.model';
import { GroupWithCourses } from '../models/groups/group-course.model';

@Injectable({ providedIn: 'root' })
export class GroupService {
  private apiUrl = 'http://localhost:8081/teachsync/groups';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt_token');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  getAll(): Observable<GroupBase[]> {
    return this.http.get<GroupBase[]>(`${this.apiUrl}/all`, { headers: this.getHeaders() });
  }

  getById(id: number): Observable<GroupBase> {
    return this.http.get<GroupBase>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  getWithCourses(id: number): Observable<GroupWithCourses> {
    return this.http.get<GroupWithCourses>(`${this.apiUrl}/${id}/courses`, { headers: this.getHeaders() });
  }

  create(dto: Partial<GroupBase>): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/create`, dto, { headers: this.getHeaders() });
  }

  update(id: number, dto: Partial<GroupBase>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/edit/${id}`, dto, { headers: this.getHeaders() });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`, { headers: this.getHeaders() });
  }

  assignToCourse(groupId: number, courseId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/assign-to-course/${groupId}/${courseId}`, {}, { headers: this.getHeaders() });
  }

  unassignFromCourse(groupId: number, courseId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/unassign-from-course/${groupId}/${courseId}`, { headers: this.getHeaders() });
  }
}