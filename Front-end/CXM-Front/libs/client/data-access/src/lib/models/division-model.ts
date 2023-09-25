import {ServiceModel} from './service-model';
import {ReturnAddress} from "./client";

export interface DivisionModel {
  id: number;
  name: string;
  clientId: number;
  address: ReturnAddress;
  services: Array<ServiceModel>;
}
