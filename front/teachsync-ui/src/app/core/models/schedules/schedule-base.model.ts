export interface ScheduleBase {
  id: number;
  startTime: number[];
  endTime: number[];
  weekDays: WeekDay[];
  groupCourseDto: GroupCourseInfo;
  classRoomBaseDto: ClassRoomInfo;
}

export type WeekDay = 'MON' | 'TUE' | 'WED' | 'THU' | 'FRI' | 'SAT' | 'SUN';

export interface GroupCourseInfo {
  id: number;
  groupId: number;
  courseId: number;
  groupName: string;
  courseName: string;
  teacherId: number;   
  teacherName: string; 
}

export interface ClassRoomInfo {
  id: number;
  name: string;
  capacity: number;
}
export interface TeacherInfo {
  id: number;
  name: string;     
  surname: string;  
  email: string;
}