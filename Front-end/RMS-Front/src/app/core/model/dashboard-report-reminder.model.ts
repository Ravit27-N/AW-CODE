import { ReminderType } from './Reminder';

export interface DashboardReportReminderModel {
  id: number;
  reminderType: ReminderType | string;
  title: string;
  dateReminder: Date;
  description: string;
  candidate: { id: number; fullName: string };
  interview: { id: number; title: string };
  active: boolean;
  interviewId: number;
  createdAt: Date;
  updatedAt: Date;
  status: string;
}
