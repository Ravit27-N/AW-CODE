import { SortDirection } from '@angular/material/sort';

export interface SharedListCriteria {
  page: number;
  pageSize: number;
  total: number;
  sortByField: string;
  sortDirection: SortDirection;
  filter: string;
}
