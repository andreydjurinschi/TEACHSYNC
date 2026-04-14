export type TopicTag = 'IT' | 'DESIGN' | 'MATH' | 'LANGUAGE' | 'BUSINESS' | 'SCIENCE' | 'OTHER';

export interface Topic {
  id: number;
  name: string;
  topicTag: TopicTag | null;
}