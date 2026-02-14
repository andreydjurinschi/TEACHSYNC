
export interface UserWithCourses{
  name: string;
  surname: string;
  email: string;
  courseNames: {
    id: number;
    name: string;
  }[];
  available: boolean
}