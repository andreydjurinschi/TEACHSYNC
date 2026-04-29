export interface NotificationItem {
  id: number;
  title: string;
  message: string;
  targetRole?: string;
  targetUserId?: number;
  targetSubject: string;
  sourceService: string;
  actionUrl?: string;
  read: boolean;
  createdAt: string;
}
