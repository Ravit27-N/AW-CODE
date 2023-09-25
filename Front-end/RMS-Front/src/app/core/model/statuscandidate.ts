export interface StatusCandidateModel {
  id?: number;
  title?: string;
  description?: string;
  active?: boolean;
  createdAt?: Date| number;
  updatedAt?: Date| number;
  deletable?: boolean;
  deleted?: boolean;
  createdBy?: string;
  links?: [
    {rel?: string; href?: string}
  ];
}

export interface StatusCandidateList {
  contents: StatusCandidateModel[];
  total: number;
  page: number;
  pageSize: number;
}

