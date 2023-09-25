import {
  CriteriaDistributionModel,
  DepositModeModel,
  DivisionModel,
  FillerModel, ReturnAddress,
  UnloadModel,
} from '@cxm-smartflow/client/data-access';

export interface ClientFormModel {
  client: {
    name: string;
    email: string;
    contactFirstName: string;
    contactLastname: string;
    file: {
      fileId: string;
      filename: string;
      fileSize: number;
    };
    address: ReturnAddress;
  };
  divisions: Array<DivisionModel>;
  functionalities: Array<string>;
  publicHolidays: Array<number>;
  unloads: Array<UnloadModel>;
  fillers: Array<FillerModel>;
  depositModes: Array<DepositModeModel>;
  criteriaDistributions: CriteriaDistributionModel;
  portalConfigEnable: boolean;
}
