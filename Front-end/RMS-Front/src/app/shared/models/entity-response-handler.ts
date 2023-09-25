export interface EntityResponseHandler<T> {
  readonly contents: T[];
  summary: any;
  page: number;
  pageSize: number;
  total: number;
}
