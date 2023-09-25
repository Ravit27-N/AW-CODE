import { Department } from './department.model';

export interface Division{
  id: number;
  name: string;
  departments: Department[]
}
//add new
export interface DivisionResponse{
  divisions: Division[];
}