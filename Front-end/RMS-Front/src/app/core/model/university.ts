export interface UniversityModel{
  id?: number;
  name?: string;
  createdAt?: Date | number;
  updatedAt?: Date | number;
  address?: string;
  description?: string;
}
export interface UniversityList{
  contents: UniversityModel[];
  page?: number;
  pageSize?: number;
  total?: number;
}
