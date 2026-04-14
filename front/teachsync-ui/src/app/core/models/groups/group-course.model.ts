export interface GroupWithCourses {
  name: string;
  date: string;
  capacity: number;
  courses: { name: string }[];
}