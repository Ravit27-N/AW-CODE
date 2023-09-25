import {DivisionModel} from './division-model';
import {UnloadModel} from './unload-model';
import {FillerModel} from './filler-model';
import {CriteriaDistributionModel} from './criteria-distribution-model';
import {DepositModeModel} from './deposit-mode-model';

export interface IClient {
  id: number;
  name: string;
  active: boolean;
  createdAt: string | Date;
  lastModified: string | Date;
  createdBy: string;
  ownerId: number;
}

export interface IClientListResponse {
  contents: Array<IClient>;
  page: number;
  pageSize: number;
  total: number;
}

export interface IClientDepositModePayload {
  value: string;
  key: string;
  scanActivation: boolean;
}

export interface IDepositModeForm {
  value: string;
  key: string;
  scanActivation: boolean;
  disabled: boolean;
}

export interface IdentificationModeForm {
  value: boolean;
  key: string;
  checked: boolean;
  disabled: boolean;
}

export interface IDepositMode {
  key: string;
  value: string;
  scanActivation: boolean;
}

export interface ReturnAddress {
  line1: string;
  line2: string;
  line3: string;
  line4: string;
  line5: string;
  line6: string;
  line7: string;
}

export interface ClientModel {
  id: number;
  name: string;
  email: string;
  contactFirstName: string;
  contactLastname: string;
  createdAt: Date;
  lastModified: Date;
  createdBy: string;
  fileId: string;
  filename: string;
  fileSize: number;
  divisions: Array<DivisionModel>;
  functionalities: Array<string>;
  publicHolidays: Array<number>;
  unloads: Array<UnloadModel>;
  fillers: Array<FillerModel>;
  depositModes: Array<DepositModeModel>;
  criteriaDistributions: CriteriaDistributionModel;
  portalConfigEnable: boolean;
  address: ReturnAddress;
}
