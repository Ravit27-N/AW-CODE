import { CandidateModel } from './candidate';

interface ProjectModel {
  id?: number;
  title: string;
  description?: string;
}

interface JobDescriptionModel {
  id?: number;
  title: string;
  description?: string;
  active?: boolean;
  file?: File;
}

export interface DemandModel {
  id?: number;
  project?: ProjectModel;
  jobDescription?: JobDescriptionModel;
  projectId: string | number;
  jobDescriptionId: string | number;
  experienceLevel?: string | number;
  nbRequired?: number;
  nbCandidates?: string;
  candidate?: CandidateModel[];
  deadLine?: Date;

  amountOfDemand?: number;
  status?: boolean; // True : In Progress and False: Completed
  createdAt?: Date;
  active?: boolean; // true : active on dashboard | false : Inactive on dashboard
}

export interface DemandDetailModel {
  createdBy: string;
  id: number;
}

export interface ListDemandModel {
  contents: DemandModel[];
  page?: number;
  pageSize?: number;
  total?: number;
}

export interface ProjectDetailModel {
  id: number;
  name: string;
  description: string;
  active: boolean;
  deleted: boolean;
  projectDetail: ProjectDetail[];
}

export interface ProjectDetail {
  jobDescription: JobDescriptionModels;
  id: number;
  nbCandidates: string;
  experienceLevel: string;
  active: boolean;
  deleted: boolean;
  status: boolean;
  nbRequired: number;
  resources: Resource[];
  deadLine: number;
  createdAt: number;
  updatedAt: number;
}

export interface JobDescriptionModels {
  id: number;
  title: string;
  description: string;
  filename: string;
  createdAt: number;
  updatedAt: number;
  active: boolean;
}

export interface Resource {
  id: number;
  firstname: string;
  lastname: string;
  gender: string;
  email: string;
  telephone: string;
  active: boolean;
  dateOfBirth: number;
  salutation: string;
  photoUrl: string;
  gpa: number;
  priority: string;
  candidateStatus: CandidateStatus;
  deleted: boolean;
}

interface CandidateStatus {
  id: number;
  title: string;
  description: string;
  active: boolean;
  createdAt: number;
  updatedAt: number;
  deleted: boolean;
  deletable: boolean;
}
