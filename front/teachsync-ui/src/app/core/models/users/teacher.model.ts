export interface TeacherDto {
  id: number;
  name: string;
  surname: string;
  email: string;
  specializations: { id: number; name: string }[];
}