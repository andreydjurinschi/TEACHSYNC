import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, ObservableLike } from 'rxjs';
import { CourseBase } from '../models/courses/course.model';
import { CourseDetailed } from '../models/courses/course-detailed.model';
import { CourseWithTeacher } from '../models/courses/course-with-teacher.model';
import { CourseWithGroups } from '../models/courses/course-group.model';

@Injectable({ providedIn: 'root' })
export class CourseService {
  private apiUrl = 'http://localhost:8081/teachsync/courses';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt_token');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  getAll(): Observable<CourseBase[]> {
    return this.http.get<CourseBase[]>(`${this.apiUrl}/all`, { headers: this.getHeaders() });
  }

  getById(id: number): Observable<CourseBase> {
    return this.http.get<CourseBase>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  getDetailed(id: number): Observable<CourseDetailed> {
    return this.http.get<CourseDetailed>(`${this.apiUrl}/${id}/info`, { headers: this.getHeaders() });
  }

  getWithTeacher(id: number): Observable<CourseWithTeacher> {
    return this.http.get<CourseWithTeacher>(`${this.apiUrl}/with-teacher/${id}`, { headers: this.getHeaders() });
  }

  create(dto: Partial<CourseBase>): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/create`, dto, { headers: this.getHeaders() });
  }

  update(id: number, dto: Partial<CourseBase>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/edit/${id}`, dto, { headers: this.getHeaders() });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`, { headers: this.getHeaders() });
  }

    assignTopic(courseId: number, topicId: number): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/assign-topic/${courseId}/${topicId}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  unassignTopic(courseId: number, topicId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/unassign-topic/${courseId}/${topicId}`,
      { headers: this.getHeaders() }
    );
  }

  assignGroup(courseId: number, groupId: number): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/assign-group/${courseId}/${groupId}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  unassignGroup(courseId: number, groupId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/unassign-group/${courseId}/${groupId}`,
      { headers: this.getHeaders() }
    );
  }

  getWithGroups(courseId: number): Observable<CourseWithGroups>{
      return this.http.get<CourseWithGroups>(`${this.apiUrl}/course-with-groups/${courseId}`, { headers: this.getHeaders() });
  }
}
