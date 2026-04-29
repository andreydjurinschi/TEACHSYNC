import { ScheduleBase } from '../schedules/schedule-base.model';

export interface TeacherBaseInfo {
  id: number;
  name?: string;
  surname?: string;
  fullName?: string;
  email: string;
}

export interface ReplacementRequest {
  id: number;
  scheduleBaseDtoRequest: ScheduleBase;
  teacherBaseInfoRequest: TeacherBaseInfo;
  groupCourseBaseInfoRequest: {
    id: number;
    groupId: number;
    courseId: number;
    groupName: string;
    courseName: string;
    categoryId?: number;
    categoryName?: string;
  };
  requestedAt: string;
  lessonDate: string;
  approvedByTeacherBaseInfoRequest?: TeacherBaseInfo;
  reason: string;
  status: 'APPROVED' | 'PENDING' | 'DECLINED';
}

export interface ReplacementCreate {
  scheduleId: number;
  teacherRequested: number;
  lessonDate: string;
  reason: string;
}
