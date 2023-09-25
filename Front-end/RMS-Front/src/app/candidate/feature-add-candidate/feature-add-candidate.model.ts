export interface FeatureAddCandidateModel {
  id?: number;
  firstname: string;
  lastname: string;
  salutation: string;
  gender: 'Male' | 'Female';
  dateOfBirth: string;
  email: string;
  photoUrl: string;
  priority: string;
  description: string;
  statusId: number;
  cvFileName: string;
  telephones: string[];
  candidateExperiences: {
    id?: number;
    companyName: string;
    position: string;
    startDate: string;
    endDate: string;
    level: string;
    projectType: string;
    technology: string;
    remarks: string;
  }[];
  candidateUniversities: {
    id?: number;
    universityId: number;
    major: string;
    startDate: string;
    endDate: string;
    graduate: boolean;
    gpa: number;
    remarks: string;
  }[];
}
