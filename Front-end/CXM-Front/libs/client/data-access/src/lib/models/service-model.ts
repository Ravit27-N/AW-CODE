import {ReturnAddress} from "./client";

export interface ServiceModel {
  id: number;
  name: string;
  address: ReturnAddress;
  divisionId: number;
}
