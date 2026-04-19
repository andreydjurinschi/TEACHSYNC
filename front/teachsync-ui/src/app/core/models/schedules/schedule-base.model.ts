export interface ScheduleBase {
  id: number;
  startTime: number[];
  endTime: number[];    
  weekDays: WeekDay[];
  teacherDto: TeacherInfo;
  groupCourseDto: GroupCourseInfo;
  classRoomBaseDto: ClassRoomInfo;
}

export type WeekDay = 'MON' | 'TUE' | 'WED' | 'THU' | 'FRI' | 'SAT' | 'SUN';

export interface TeacherInfo {
  id: number;
  name: string;     
  surname: string;  
  email: string;
}

export interface GroupCourseInfo {
  id: number;
  groupId: number;
  courseId: number;
  groupName: string;
  courseName: string;
}

export interface ClassRoomInfo {
  id: number;
  name: string;
  capacity: number;
}