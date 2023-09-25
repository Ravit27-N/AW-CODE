export interface UserGroup {
  id?: string;
  name: string;
  clientRoles?: Record<string, unknown>;
}

export interface DefaultGroupCriteria {
  pageIndex: number;
  pageSize: number;
  sortByField: string;
  sortDirection: string;
}

export interface GroupCriteria {
  defaultCriteria: DefaultGroupCriteria;
  filter: string;
}
