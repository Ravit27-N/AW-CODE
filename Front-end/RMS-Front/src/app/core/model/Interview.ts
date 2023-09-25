import { ResultRankEnum } from './constant';

export interface Interview {
  id: number;
  title: string;
  description?: string;
  candidate: { id: number | string; fullName: string; photoUrl?: string };
  dateTime: Date | string;
  status?: string;
  statusId?: number;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  reminderCount?: number;
  hasResult?: boolean;
}

export interface InterviewList {
  contents: Interview[];
  total: number;
  page: number;
  pageSize: number;
}

export interface InterviewFormModel {
  candidateId: number | string;
  status?: string;
  statusId?: number;
  dateTime: Date | string;
  sendInvite?: boolean;
  setReminder?: boolean;
  reminderTime?: number;
  title: string;
  description?: string;
}

export interface InterviewResult {
  score: any;
  average?: number;
  english: string;
  logical: string;
  flexibility: string;
  oral: string;
  remark?: string;
}

export interface InterviewResultForm {
  quizScore: number;
  quizMax: number;
  codeScore: number;
  codeMax: number;
  avarage: number;
  english: ResultRankEnum | string;
  logical: ResultRankEnum | string;
  flexibily: ResultRankEnum | string;
  qa: ResultRankEnum | string;
  remark?: string;
}

export interface DefaultInterviewCriteria {
  pageIndex: number;
  pageSize: number;
  sortByField: string;
  sortDirection: string;
}

export interface InterviewCriteria {
  defaultCriteria: DefaultInterviewCriteria;
  filter: string;
  sortDirection?: string;
  sortByField?: string;
  startDate?: string;
  originalStartDate?: Date;
  endDate?: string;
  originalEndDate?: Date;
  status?: string[];
  option?: number;
}
