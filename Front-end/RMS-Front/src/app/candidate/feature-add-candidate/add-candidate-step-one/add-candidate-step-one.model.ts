export interface AddCandidateStepOneModel {
  error: boolean;
  errorMessage: string;
  value: string;
}

export interface CandidateInformation {
  id: number;
  salutation: string;
  firstName: string;
  lastName: string;
  dateOfBirth: Date;
  gender: boolean;
  email: string;
  phoneNumbers: string[];
  priority: string;
  status: number;
  profileFileId: string;
  profileFileBase64: string;
  profileFileExtension: string;
  profileURL: string;
  description: string;
}
