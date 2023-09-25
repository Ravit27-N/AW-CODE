export interface  JobDescriptionModel {
  id?: number|string;
  name: string;
  description?: string;
  active?: boolean;
  deleted?: boolean;
  createdAt?: Date;
}

export  interface ListJobDescriptionModel{
  contents: JobDescriptionModel[];
  page?: number;
  pageSize?: number;
  total?: number;
}
