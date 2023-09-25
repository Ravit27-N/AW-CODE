export interface ProfileStorageModel {
  page?: number;
  pageSize?: number;
  sortByField?: string;
  sortDirection?: string;
  clientIds?: number[],
  filter?: string
}
