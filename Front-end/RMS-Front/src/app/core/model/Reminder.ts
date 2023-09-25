export enum ReminderType {
  normal = 'Normal', specail = 'Specail', interview = ' Interview '
}

export interface ReminderModel {
  id?: number;
  reminderType: ReminderType | string;
  title: string;
  dateReminder?: Date | string;
  description?: string;
  candidate?: { id: number; fullName: string };
  interview?: {id: number; title: string};
  active?: boolean;
  interviewId?: number;
  createdAt?: Date | number;
  updatedAt?: Date | number;
  status?: string;
}

export interface ReminderList {
  contents: ReminderModel[];
  total: number;
  page: number;
  pageSize: number;
}

export interface ReminderFormModel {
  id?: number;
  userId?: number | string;
  candidateId?: number | string;
  interviewId?: number | string;
  reminderType: ReminderType | string;
  title: string;
  description?: string;
  dateReminder?: Date | string;
  active?: boolean;
  status?: string;
}
