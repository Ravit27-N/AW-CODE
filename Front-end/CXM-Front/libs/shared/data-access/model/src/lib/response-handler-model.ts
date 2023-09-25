export interface EntityResponseHandler<T> {
  contents?: T[];
  page?: number;
  pageSize?: number;
  total?: number;
  isLoading?: boolean;
}
