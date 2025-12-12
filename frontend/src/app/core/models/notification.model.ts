export enum NotificationType {
  CAMPAIGN_CONFIRMATION = 'CAMPAIGN_CONFIRMATION',
  EVENT_REGISTRATION = 'EVENT_REGISTRATION',
  EVENT_REMINDER = 'EVENT_REMINDER',
  PARTICIPATION_CONFIRMATION = 'PARTICIPATION_CONFIRMATION',
  MARKETING_CAMPAIGN = 'MARKETING_CAMPAIGN',
  SYSTEM_ALERT = 'SYSTEM_ALERT'
}

export enum NotificationChannel {
  EMAIL = 'EMAIL',
  SMS = 'SMS',
  PUSH = 'PUSH',
  IN_APP = 'IN_APP'
}

export enum NotificationStatus {
  PENDING = 'PENDING',
  SENT = 'SENT',
  DELIVERED = 'DELIVERED',
  FAILED = 'FAILED',
  RETRY = 'RETRY'
}

export interface Notification {
  id?: number;
  recipientId?: number;
  recipientEmail?: string;
  type: NotificationType;
  channel: NotificationChannel;
  subject?: string;
  content?: string;
  status?: NotificationStatus;
  sentDate?: string;
  deliveryStatus?: string;
  errorMessage?: string;
  retryCount?: number;
  createdAt?: string;
}
