export type TopicTag = 'IT' | 'DESIGN' | 'MATH' | 'LANGUAGE' | 'BUSINESS' | 'SCIENCE';

export interface TopicBase {
  id: number;
  name: string;
  topicTag: TopicTag;
}

export interface CourseDetailedMy {
  id: number;
  name: string;
  description?: string;
  photoUrl?: string;
  categoryId?: number;
  categoryName?: string;
  topics: TopicBase[]; 
  groups: { name: string }[];
}