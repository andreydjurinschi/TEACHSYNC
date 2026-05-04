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

export interface UserActivity {
  id: number;
  eventId: string;
  sourceService: string;
  actionType: string;
  targetUserId?: number;
  targetRole?: string;
  actorUserId?: number;
  actorName?: string;
  title: string;
  summary: string;
  details?: string;
  actionUrl?: string;
  createdAt: string;
}
