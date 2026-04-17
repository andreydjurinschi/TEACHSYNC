import { GroupBase } from "../groups/group.model";

export interface CourseWithGroups {
  id: number;
  name: string;
  description: string;
  photoUrl?: string;
  teacherId?: number;
  categoryId?: number;
  categoryName?: string;
  groups: GroupBase[];
}