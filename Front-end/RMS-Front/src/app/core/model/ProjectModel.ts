export interface  ProjectModel{
  id?: number|string;
  name?: string;
  description?: string;
  active?: boolean;
  deleted?: boolean;
  createdAt?: Date;
}

export  interface ListProjectModel{
  contents: ProjectModel[];
  page?: number;
  pageSize?: number;
  total?: number;
}

interface DefaultCriteria {
  pageIndex: number;
  pageSize: number;
  sortByField: string;
  sortDirection: string;
}

export interface ProjectCriteria {
  defaultCriteria: DefaultCriteria;
  filter: string;
  startDate?: string;
  endDate?: string;
  sortDirection?: string;
  sortByField?: string;
  isArchive?: boolean;
  status?: string[];
  option?: number;
  originalStartDate?: string;
  originalEndDate?: string;
}
