import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin } from 'rxjs';
import {
  CourseStatistics,
  ReplacementStatistics,
  ScheduleStatistics,
  TeacherReplacementStatistics,
  TeacherWorkloadStatistics,
  UserStatistics
} from '../models/statistics/profile-statistics.model';

@Injectable({ providedIn: 'root' })
export class ProfileStatisticsService {
  private api = 'http://localhost:8080/teachsync';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt_token');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  getAdminStatistics() {
    const options = { headers: this.getHeaders() };
    return forkJoin({
      users: this.http.get<UserStatistics>(`${this.api}/users/statistics`, options),
      courses: this.http.get<CourseStatistics>(`${this.api}/courses/statistics`, options),
      schedules: this.http.get<ScheduleStatistics>(`${this.api}/schedules/statistics`, options),
      replacements: this.http.get<ReplacementStatistics>(`${this.api}/replacements/statistics`, options)
    });
  }

  getManagerStatistics() {
    return this.http.get<ReplacementStatistics>(`${this.api}/replacements/statistics`, { headers: this.getHeaders() });
  }

  getTeacherStatistics(teacherId: number) {
    const options = { headers: this.getHeaders() };
    return forkJoin({
      workload: this.http.get<TeacherWorkloadStatistics>(`${this.api}/schedules/statistics/teacher/${teacherId}`, options),
      replacements: this.http.get<TeacherReplacementStatistics>(`${this.api}/replacements/statistics/teacher/${teacherId}`, options)
    });
  }
}
