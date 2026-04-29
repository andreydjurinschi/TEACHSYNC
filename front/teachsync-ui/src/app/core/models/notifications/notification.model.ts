export interface NotificationItem {
  id: number;
  title: string;
  message: string;
  targetRole?: 'ADMIN' | 'MANAGER' | 'TEACHER' | null;
  targetUserId?: number | null;
  targetSubject: string;
  sourceService: string;
  createdAt: string;
  read: boolean;
}
