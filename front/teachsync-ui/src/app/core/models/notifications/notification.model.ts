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

export interface NotificationPreference {
  userId: number;
  scheduleEnabled: boolean;
  replacementEnabled: boolean;
  courseEnabled: boolean;
  systemEnabled: boolean;
  realtimeEnabled: boolean;
  importantOnly: boolean;
}
