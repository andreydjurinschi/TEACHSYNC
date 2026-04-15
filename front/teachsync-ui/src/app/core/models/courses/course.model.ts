export interface CourseBase {
  id: number;
  name: string;
  description: string;
  photoUrl?: string;
  teacherId?: number;
  categoryId?: number;
  categoryName?: string;
}