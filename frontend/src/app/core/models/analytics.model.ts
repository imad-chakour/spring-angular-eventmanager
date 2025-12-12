export interface CampaignMetrics {
  id?: number;
  campaignId: number;
  campaignReference?: string;
  emailsSent?: number;
  emailsDelivered?: number;
  emailsOpened?: number;
  clicks?: number;
  conversions?: number;
  bounceRate?: number;
  openRate?: number;
  clickRate?: number;
  conversionRate?: number;
  calculationDate?: string;
}

export interface EventMetrics {
  id?: number;
  eventId: number;
  totalRegistrations?: number;
  confirmedRegistrations?: number;
  actualAttendance?: number;
  attendanceRate?: number;
  cancellationRate?: number;
  satisfactionScore?: number;
  calculationDate?: string;
}
