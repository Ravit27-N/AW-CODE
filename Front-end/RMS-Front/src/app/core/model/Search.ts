import { UniversityModel } from './university';

export interface IAdvanceSearchResultItem {
  candidateStatus: { id: number; title: string };
  countInterview: number;
  countReminder: number;
  description: string;
  email: string;
  fullName: string;
  gender: string;
  gpa: number;
  yearOfExperience: number;
  dateOfBirth: string;
  id: number;
  firstname: string;
  lastname: string;
  photoUrl: string;
  statusId: string;
  telephone: string;
  universities: UniversityModel[];
  interviews: {
    id: number;
    lastInterview: string;
    result: any;
    title: string;
  };
}
