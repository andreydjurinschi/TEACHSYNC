export interface UserStatistics {
  totalUsers: number;
  adminCount: number;
  managerCount: number;
  teacherCount: number;
}

export interface CourseStatistics {
  totalCourses: number;
  coursesWithTeacher: number;
  coursesWithoutTeacher: number;
  groupCourseRelations: number;
}

export interface ScheduleStatistics {
  totalSchedules: number;
  scheduledGroupCourses: number;
  unscheduledGroupCourses: number;
  totalGroupCourses: number;
  classrooms: number;
}

export interface TeacherHelpMetric {
  teacherId: number;
  teacherName: string;
  approvedReplacements: number;
  declinedReplacements: number;
  pendingInvitations: number;
}

export interface ReplacementStatistics {
  totalRequests: number;
  pendingRequests: number;
  approvedRequests: number;
  declinedRequests: number;
  expiredRequests: number;
  cancelledRequests: number;
  autoClosedRequests: number;
  topHelpers: TeacherHelpMetric[];
}

export interface TeacherWorkloadStatistics {
  weeklyLessons: number;
  weeklyHours: number;
  scheduledCourses: number;
  scheduledGroups: number;
}

export interface TeacherReplacementStatistics {
  requestedReplacements: number;
  helpedReplacements: number;
  pendingInvitations: number;
  declinedInvitations: number;
}
