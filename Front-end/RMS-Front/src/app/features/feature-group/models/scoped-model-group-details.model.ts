export interface ScopedModelDemandDetails {
  id: number;
  projectId: number;
  project: {
    id: number;
    name: string;
    description: string;
    active: boolean;
    deleted: boolean;
  };
  jobDescriptionId: number;
  jobDescription: {
    id: number;
    title: string;
    description: string;
    filename: string;
    active: boolean;
  };
  nbRequired: number;
  experienceLevel: string;
  deadLine: Date;
  nbCandidates: string;
  candidateId: number;
  candidate: any;
  status: boolean;
  active: boolean;
  createdAt: Date;
  updatedAt: Date;
  createdBy: Date;
  deleted: boolean;
}
