export interface IEnvelope<T> {
  contents: T[];
  page: number;
  pageSize: number;
  total: number;
}
