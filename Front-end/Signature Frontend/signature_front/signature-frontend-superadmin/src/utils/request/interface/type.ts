export const Verb = {
  Get: 'GET',
  Post: 'POST',
  Put: 'PUT',
  Delete: 'DELETE',
  Patch: 'PATCH',
};

export type IDType = number | string;
export interface BaseQuery {
  page:number;
  pageSize:number;
  
}