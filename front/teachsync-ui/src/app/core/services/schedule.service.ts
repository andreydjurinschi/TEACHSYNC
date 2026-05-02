import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ClassRoomInfo, GroupCourseInfo, ScheduleBase, TeacherInfo } from '../models/schedules/schedule-base.model';

@Injectable({ providedIn: 'root' })
export class ScheduleService {
    private base = 'http://localhost:8080/teachsync/schedules';

    constructor(private http: HttpClient) { }

    private getHeaders(): HttpHeaders {
        const token = localStorage.getItem("jwt_token")
        return new HttpHeaders({
            Authorization: `Bearer ${token}`
        })
    }
    getAll(): Observable<ScheduleBase[]> {
        return this.http.get<ScheduleBase[]>(`${this.base}/all`, { headers: this.getHeaders() });
    }

    getById(id: number): Observable<ScheduleBase> {
        return this.http.get<ScheduleBase>(`${this.base}/${id}`, { headers: this.getHeaders() });
    }

    getAllTeachers(): Observable<TeacherInfo[]> {
        return this.http.get<TeacherInfo[]>(`${this.base}/teachers/all`, { headers: this.getHeaders() });
    }

    getAllGroupCourses(): Observable<GroupCourseInfo[]> {
        return this.http.get<GroupCourseInfo[]>(`${this.base}/group-courses/all`, { headers: this.getHeaders() });
    }

    getAllClassrooms(): Observable<ClassRoomInfo[]> {
        return this.http.get<ClassRoomInfo[]>(`${this.base}/classrooms/all`, { headers: this.getHeaders() });
    }

    create(dto: any): Observable<void> {
        return this.http.post<void>(`${this.base}/create`, dto, { headers: this.getHeaders() });
    }

    update(id: number, dto: any): Observable<ScheduleBase> {
        return this.http.put<ScheduleBase>(`${this.base}/${id}`, dto, { headers: this.getHeaders() });
    }

    checkClassroomConflicts(
        days: string[], startTime: string, endTime: string
    ): Observable<number[]> {
        const params = { days: days.join(','), startTime, endTime };
        return this.http.get<number[]>(
            `${this.base}/classrooms/conflicts`,
            { headers: this.getHeaders(), params }
        );
    }

    getGroupSize(groupCourseId: number): Observable<number> {
        return this.http.get<number>(
            `${this.base}/group-courses/${groupCourseId}/size`, { headers: this.getHeaders() }
        );
    }

    getMySchedule(): Observable<ScheduleBase[]> {
  return this.http.get<ScheduleBase[]>(
    `${this.base}/my-schedule`,
    { headers: this.getHeaders() }
  );
}

    getUnscheduledGroupCourses(): Observable<GroupCourseInfo[]> {
  return this.http.get<GroupCourseInfo[]>(
    `${this.base}/group-courses/group-without-mention-in-schedule`,
    { headers: this.getHeaders() }
  );
}
}
