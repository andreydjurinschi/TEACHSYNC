
export interface UserWithCourses{
  id: number;
  name: string;
  surname: string;
  email: string;
  courseNames: {
    id: number;
    name: string;
    description: string
  }[];
  available: boolean
}