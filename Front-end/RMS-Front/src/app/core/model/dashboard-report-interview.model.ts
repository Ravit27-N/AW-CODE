export interface DashboardReportInterviewModel {
  id: number;
  title: string;
  description: string;
  candidate: { id: number; fullName: string; photoUrl: string, shortName: string };
  dateTime: Date | string;
  status: string;
  statusId: number;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  reminderCount: number;
  hasResult: boolean;
}
