import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService, CxmFlowTraceabilityService } from '@cxm-smartflow/shared/data-access/api';
import { FlowTraceability, UserModel } from '@cxm-smartflow/shared/data-access/model';
import * as envFileManager from '@env-cxm-file-manager';
import { flowTraceabilityEnv } from '@env-flow-traceability';
import { Observable } from 'rxjs';
import {
  FlowDocumentDetailModel,
  FlowDocumentElementAssociation,
  FlowDocumentFilterCriteriaModel,
  FlowDocumentList,
  FlowFilterCriteriaParams,
  FlowTraceabilityFilterCriterialModel,
  FlowTraceabilityList,
  FlowTraceabilityModel,
  FlowTraceabilitySubChannelModel
} from '../models';
import { FlowTraceabilityDepositFlow } from '../models/FlowTraceabilityDepositFlow';
import { EventHistoryInfo } from '@cxm-smartflow/flow-traceability/ui/featured-flow-event-history';

@Injectable({
  providedIn: 'root',
})
export class FlowTraceabilityService {
  constructor(
    private cxmFlowTraceabilityService: CxmFlowTraceabilityService,
    private apiService: ApiService
  ) {}

  /**
   * Method used to get obect of {@link FlowTraceabilityList} by param of {@link FlowTraceabilityParams} with pagination.
   * @param page
   * @param pageSize
   * @param params
   * @returns object of {@link FlowTraceabilityList}
   */
  getFlowTraceabilityList(
    page: number,
    pageSize: number,
    params?: FlowFilterCriteriaParams
  ): Observable<FlowTraceabilityList> {
    let params$ = new HttpParams();
    if (params !== undefined) {
      Object.entries(params).forEach(([key, value]) => {
        params$ = params$.set(key, value);
      });
    }
    return this.cxmFlowTraceabilityService.get(
      `${flowTraceabilityEnv.flowTraceabilityContext}/flow-traceability/${page}/${pageSize}`,
      params$
    );
  }


  /**
   * Method used to update status property of {@link FlowTraceabilityModel}.
   * @param id
   * @param status
   * @param server
   * @returns object of {@link FlowTraceabilityModel}.
   */
  updateStatus(
    id: number,
    status: string,
    server: string
  ): Observable<FlowTraceabilityModel> {
    return this.cxmFlowTraceabilityService.patch(
      `${flowTraceabilityEnv.flowTraceabilityContext}/flow-traceability/status/${id}/${status}/${server}`
    );
  }


  /**
   * Method used to get all user.
   * @returns array of {@link UserModel}
   */
  getUsers(): Observable<UserModel[]> {
    // let params$ = new HttpParams();
    // if (serviceId !== undefined) {
    //   params$ = params$.set('serviceId', serviceId);
    // }
    return this.cxmFlowTraceabilityService.get(
      `${flowTraceabilityEnv.flowTraceabilityContext}/flow-traceability/users`
    );
  }


  /**
   * Method used to get all filter criterail.
   * @returns array of {@link FlowTraceabilityFilterCriterialModel}
   */
  getFlowTraceabilityFilterCriteria(): Observable<FlowTraceabilityFilterCriterialModel> {
    return this.cxmFlowTraceabilityService.get(
      `${flowTraceabilityEnv.flowTraceabilityContext}/flow-traceability/filter-criteria`
    );
  }

  /**
   * Method used to get sub channel.
   * @param channel
   * @returns
   */
  getFlowTraceabilitySubChannel(
    channel: string
  ): Observable<FlowTraceabilitySubChannelModel> {
    let params$ = new HttpParams();
    if (channel !== undefined) {
      params$ = params$.set('channel', channel);
    }
    return this.cxmFlowTraceabilityService.get(
      `${flowTraceabilityEnv.flowTraceabilityContext}/flow-traceability/sub-channel`,
      params$
    );
  }

  /**
   * Method used to get object of {@link FlowDocumentList} by params of {@link FlowFilterCriteriaParams}.
   * @param page
   * @param pageSize
   * @param params
   * @return object of {@link FlowDocumentList}
   */
  getFlowDocumentPagination(
    flowTraceabilityId: number,
    page: number,
    pageSize: number,
    params?: FlowFilterCriteriaParams
  ): Observable<FlowDocumentList> {
    let params$ = new HttpParams();
    if (params !== undefined) {
      Object.entries(params).forEach(([key, value]) => {
        params$ = params$.set(key, value);
      });
    }
    if (flowTraceabilityId === undefined || flowTraceabilityId === 0) {
      return this.cxmFlowTraceabilityService.get(
        `${flowTraceabilityEnv.flowTraceabilityContext}/flow-document/${page}/${pageSize}`,
        params$
      );
    } else {
      return this.cxmFlowTraceabilityService.get(
        `${flowTraceabilityEnv.flowTraceabilityContext}/flow-document/${flowTraceabilityId}/${page}/${pageSize}`,
        params$
      );
    }
  }

  getDocumentDetailsPagination(
    flowTraceabilityId: number,
    params?: FlowFilterCriteriaParams
  ): Observable<Array<number>> {
    let params$ = new HttpParams();
    if (params !== undefined) {
      Object.entries(params).forEach(([key, value]) => {
        params$ = params$.set(key, value);
      });
    }
    return this.cxmFlowTraceabilityService.get(
      `${flowTraceabilityEnv.flowTraceabilityContext}/flow-document/ids/${flowTraceabilityId}`,
      params$
    );
  }

  /**
   * Method used to get filter criteria of flow document.
   * @returns object of {@link FlowDocumentFilterCriteriaModel}
   */
  getFlowDocumentFilterCriteria(
    channel: string
  ): Observable<FlowDocumentFilterCriteriaModel> {
    let params$ = new HttpParams();
    if (channel !== undefined) {
      params$ = params$.set('channel', channel);
    }
    return this.cxmFlowTraceabilityService.get(
      `${flowTraceabilityEnv.flowTraceabilityContext}/flow-document/filter-criteria`,
      params$
    );
  }

  /**
   * Method used to get list of element association.
   * @param flowDocumentId
   * @return list of {@link FlowDocumentElementAssociation}
   */
  getFlowDocumentOfElementAssociationList(
    flowDocumentId: number
  ): Observable<FlowDocumentElementAssociation[]> {
    return this.cxmFlowTraceabilityService.get(
      `${flowTraceabilityEnv.flowTraceabilityContext}/element-association/${flowDocumentId}`
    );
  }

  /**
   * Method used to get flow document detail.
   * @param id
   * @return object of {@link FlowDocumentDetailModel}
   */
  getFlowDocumentDetail(id: number): Observable<FlowDocumentDetailModel> {
    return this.cxmFlowTraceabilityService.get(
      `${flowTraceabilityEnv.flowTraceabilityContext}/flow-document/details/${id}`
    );
  }

  getBase64File(fileId: string, flowType?: string, type?: string) {
    let params$ = new HttpParams();
    if (flowType !== undefined && ['sms','email'].includes(flowType.toLowerCase())) {
      params$ = params$.set('flowType', flowType);
    }
    if (type) {
      params$ = params$.set('type', type);
    }

    return this.cxmFlowTraceabilityService.base64File(
      `${flowTraceabilityEnv.flowTraceabilityContext}/storage/file/${fileId}`,
      params$
    );
  }

  getComposedIdAndStep(
    flowTraceabilityId: number
  ): Observable<FlowTraceabilityDepositFlow> {
    return this.cxmFlowTraceabilityService.get(
      `${flowTraceabilityEnv.flowTraceabilityContext}/flow-traceability/deposit-info/${flowTraceabilityId}`
    );
  }

  getCampaignByFileId(fileId: string): Observable<any> {
    return this.cxmFlowTraceabilityService.get(
      `${flowTraceabilityEnv.campaignContext}/campaigns/find-by/${fileId}`
    );
  }

  getFlowTraceabilityById(id: number): Observable<any> {
    return this.cxmFlowTraceabilityService.get(
      `${flowTraceabilityEnv.flowTraceabilityContext}/flow-traceability/${id}`
    );
  }

  downloadAssociateDocument(fileId: string): Observable<any> {
    const params = new HttpParams()
      .set('funcKey', FlowTraceability.CXM_FLOW_TRACEABILITY)
      .set('privKey', FlowTraceability.OPEN_AND_DOWNLOAD_RELATED_ITEM.replace(`${FlowTraceability.CXM_FLOW_TRACEABILITY}_`, ''));

    return this.apiService.get(
      `${envFileManager.fileManagerEnv.fileManagerContext}/file-manager/file/${fileId}`, params
    );
  }

  getFlowCampaignId(id: number): Observable<any> {
    return this.cxmFlowTraceabilityService.get(
      `${flowTraceabilityEnv.flowTraceabilityContext}/flow-traceability/${id}/campaign`
    );
  }

  deleteFlowDeposit(fileId: number | string): Observable<any> {
    return this.cxmFlowTraceabilityService.patch(
      `${flowTraceabilityEnv.flowTraceabilityContext}/flow-deposit/delete/${fileId}`
    );
  }

  loadAllUserOfDepositByService(serviceId?: number): Observable<UserModel[]> {
    let params$ = new HttpParams();
    if (serviceId) {
      params$ = params$.set('serviceId', serviceId);
    }
    return this.cxmFlowTraceabilityService.get(
      `${flowTraceabilityEnv.flowTraceabilityContext}/flow-traceability/users/deposit`,
      params$
    );
  }

  cancelFlowDepositPortal(uuid: string, ownerId: number): Observable<any> {
    const params = new HttpParams().set('ownerId', ownerId);
    return this.cxmFlowTraceabilityService.put(
      `${flowTraceabilityEnv.processControlContext}/process-control/cancel/${uuid}`,
      null,
      params
    );
  }

  getClientFiller(): Observable<any> {
    return this.cxmFlowTraceabilityService.get(`${flowTraceabilityEnv.flowTraceabilityContext}/flow-document/client-fillers`)
  }

  getStatusInfo(id: number, locale: string): Observable<EventHistoryInfo> {
    const params = new HttpParams().set('locale', locale);
    return this.cxmFlowTraceabilityService.get(`${flowTraceabilityEnv.flowTraceabilityContext}/flow-document/status-info/${id}`, params);
  }
  

  exportSuiviToCSV(
    channels: string[],
    categories: string[],
    fillers: string[],
    status: string,
    filter: string,
    startDate: string,
    endDate: string,
    sortByField: string,
    sortDirection: string,
    page: number,
    pageSize: number
  ): Observable<any> {
    const channelsParam = channels.length > 0 ? channels.join(',') : '';
    const categoriesParam = categories.length > 0 ? categories.join(',') : '';
    const fillersParam = fillers.length > 0 ? fillers.join(',') : '';
    const startDateParam = startDate || ''; 
    const endDateParam = endDate || ''; 
    const params = new HttpParams()
      .set('channels', channelsParam)
      .set('categories', categoriesParam)
      .set('fillers', fillersParam)
      .set('status', status)
      .set('filter', filter)
      .set('startDate', startDateParam)
      .set('endDate', endDateParam)
      .set('sortDirection', sortDirection)
      .set('sortByField', sortByField);
  
    return this.cxmFlowTraceabilityService.fileCsv(
      `${flowTraceabilityEnv.flowTraceabilityContext}/flow-document/${page}/${pageSize}/export`,
      params
    );
  }
  

}
