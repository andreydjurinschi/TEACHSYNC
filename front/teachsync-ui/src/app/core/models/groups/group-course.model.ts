export interface GroupWithCourses {
  id: number
  name: string;
  date: string;
  capacity: number;
  courses: { id: number, name: string, description: string }[];
}