export interface CourseWithTeacher {
  name: string;
  description: string;
  teacherRequest: {
    id: number;
    name: string;
    surname: string;
    email: string;
  };
}
