export interface CourseDetailed {
  name: string;
  description: string;
  categoryName: string;
  topics: { name: string }[];
  groups: { name: string }[];
}

