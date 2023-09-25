import { Division } from './division.model';

export interface Client{
  id: number;
  name: string;
  divisions: Division[]
}

export interface ClientResponse {
  clients: Client[];
}
