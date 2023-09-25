import { ConfigurationForm } from './configuration-form';
import { IClientDepositModePayload } from './client';
import { CriteriaDistributionFormModel } from './criteria-distribution-form-model';
import { PreferencePayload } from './criteria-distribution-model';
import { PostalConfigurationVersion } from './postal-configuration-version.model';

export interface InitialClientStateModel {
  mode: 0 | 1;
  clientId?: string;
  client: {
    name: string, contactFirstName: string,
    contactLastname: string,
    email: string, file: any
  };
  divisions: Array<{
    name: string, _editable: boolean, _deletable: boolean,
    services: Array<string>
  }>;
  upload: {
    progressing: boolean, uploaded: boolean
  };
  navigation: {
    allstep: number,
    step: number,
    canNext: boolean,
    canPrev: boolean,
    nextButton: string, prevButton: string,
    ready: boolean,
    isAdmin: boolean
  };
  functionalities: Array<string>;
  offloadConfig: { byDays: any[], byHolidays: any[], ready: boolean };
  fillers: Array<{ key: string, enabled: boolean, value: string }>;
  depositModes: [];
  depositModesPayload: IClientDepositModePayload[];
  configurations: ConfigurationForm[];
  configurationVersion: PostalConfigurationVersion[];
  configuration_clientName: string;
  portalConfigEnable: boolean;
  beforeClientModify: any;
  beforeConfigurationsChanged: ConfigurationForm[];
  criteriaDistributions: Array<CriteriaDistributionFormModel>;
  criteriaDistributionsSnapshot: Array<CriteriaDistributionFormModel>;
  criteriaDistributionPayload: PreferencePayload;
  modifyDocId: string;
  isLocked: boolean;
  referenceConfigurationVersion: number | undefined;
  isPreviewConfigurationMode: boolean;
  configurationHasModified: boolean;
}
