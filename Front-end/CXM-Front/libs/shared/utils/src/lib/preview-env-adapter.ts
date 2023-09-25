import { IAppSettings } from '@cxm-smartflow/shared/app-config';
import { Observable, of } from 'rxjs';
import { flowTraceabilityEnv } from '@env-flow-traceability';
import { cxmProfileEnv } from '@env-cxm-profile';
import { directoryEnv } from '@env-cxm-directory';
import { fileManagerEnv } from '@env-cxm-file-manager';
import { templateEnv } from '@env-cxm-template';
import { flowDepositEnv } from '@env-flow-deposit';
import { settingEnv } from '@env-cxm-setting';
import { campaignEnv } from '@env-cxm-campaign';

export enum API_TYPE {
  FLOW_TRACEABILITY = 1,
  PROFILE = 2,
  DIRECTORY = 3,
  FILE_MANAGER = 4,
  TEMPLATE = 5,
  DEPOSIT = 6,
  CAMPAIGN = 7,
  SETTING = 8,
}

export interface ApiEnvironment {
  apiURL: string;
  contextPath: string;
}

export class PreviewEnvAdapter {

  public static environment(
    apiType: number,
    fileId: string,
    setting: IAppSettings
  ): Observable<ApiEnvironment> {
    let apiURL = '';
    let path = '';

    if (apiType == API_TYPE.FLOW_TRACEABILITY) {
      apiURL = setting.apiGateway;
      path = flowTraceabilityEnv.flowTraceabilityContext;
      path = `${flowTraceabilityEnv.flowTraceabilityContext}/storage/file/${fileId}`;
    }
    // TODO: We can switch apiURL & contextPath by using (apiType) base on business requirement.
    else if (apiType == API_TYPE.PROFILE) {
      apiURL = setting.apiGateway;
      path = cxmProfileEnv.profileContext;
    } else if (apiType == API_TYPE.DIRECTORY) {
      apiURL = setting.apiGateway;
      path = directoryEnv.directoryContext;
    } else if (apiType == API_TYPE.FILE_MANAGER) {
      apiURL = setting.apiGateway;
      path = fileManagerEnv.fileManagerContext;
    } else if (apiType == API_TYPE.TEMPLATE) {
      apiURL = setting.apiGateway;
      path = templateEnv.templateContext;
    } else if (apiType == API_TYPE.DEPOSIT) {
      apiURL = setting.apiGateway;
      path = flowDepositEnv.flowDepositContext;
    } else if (apiType == API_TYPE.SETTING) {
      apiURL = setting.apiGateway;
      path = `${settingEnv.settingContext}/resources/file/${fileId}`;
    } else if (apiType == API_TYPE.CAMPAIGN) {
      apiURL = setting.apiGateway;
      path = campaignEnv.campaignContext;
    } else {
      apiURL = setting.apiGateway;
      path = `${flowTraceabilityEnv.flowTraceabilityContext}/storage/file/${fileId}`;
    }

    return of({
      apiURL: apiURL,
      contextPath: path
    });
  }
}
