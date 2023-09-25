export interface ScopedDemandListItem {
  id: number;
  nbCandidates: string;
  experienceLevel: string;
  active: boolean;
  status: boolean;
  nbRequired: number;
  resources: number[];
  deadLine: Date;
  createdAt: Date;
  updatedAt: Date;
  jobDescription: {
    id: number;
    title: string;
    description: string;
    filename: string;
    active: boolean;
  };
  project: {
    id: number;
    name: string;
    description: string;
    active: boolean;
    deleted: boolean;
  };
  deleted: boolean;
}
