export interface ActivityModel {
  id?: number | string;
  title?: string;
  description?: string;
  createdAt?: Date | number;
  updatedAt?: Date | number;
  candidate?: {
    id: number;
    fullname: string;
    gender?: string;
    status?: {
      id?: number;
      title?: string;
      active?: boolean;
    };
    photoUrl?: any;
  };
  author?: string;
  links?: {
    getActivity?: { rel: string; href: string };
    update?: { rel: string; href: string };
    delete?: { rel: string; href: string };
  };
}

export interface ActivityList {
  contents: ActivityModel[];
  total: number;
  page: number;
  pageSize: number;
}

export interface ActivityFormModel {
  id?: number | string;
  candidateId: number;
  statusId: number;
  status?: string;
  active?: boolean;
  title?: string;
  description?: string;
}

export interface ActivityCriteriaModel {
  defaultCriteria: DefaultActivity;
  filter?: string;
}

export interface DefaultActivity {
  pageIndex: number;
  pageSize: number;
  sortByField: string;
  sortDirection: string;
}
