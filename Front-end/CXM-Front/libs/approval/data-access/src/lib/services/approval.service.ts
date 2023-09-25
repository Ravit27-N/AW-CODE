import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CxmFlowTraceabilityService } from '@cxm-smartflow/shared/data-access/api';
import { flowTraceabilityEnv } from '@env-flow-traceability';
import { Observable } from 'rxjs';
import { ListFlowApprovalReposne } from '../models/approval.model';
import { ApproveDocResponse } from './../models/approval-doc.model';
import {RemainingShipmentModel, StatusUtils} from "../models";

@Injectable()
export class ApprovalService {

  getAll(filters?: {
    filter: string,
    page: number,
    pageSize: number,
    sortDirection: 'asc'|'desc',
    sortByField: string,
    categories: string[],
    users: string[],
    start: string, end: string,
    channels: string[]
  }): Observable<ListFlowApprovalReposne>
  {
    let params = new HttpParams();

    if(filters) {
      if(filters.filter) {
        params = params.set('filter', filters.filter);
      }
      if(filters.sortByField) {
        params = params.set('sortByField', filters.sortByField);
      }
      if(filters.sortDirection) {
        params = params.set('sortDirection', filters.sortDirection);
      }

      if(filters.categories) {
        params = params.set('categories', filters.categories.join(','));
      }

      if(filters.users) {
        params = params.set('users', filters.users.join(','));
      }

      if(filters.channels) {
        params = params.set('channels', filters.channels.join(','));
      }

      if(filters.end || filters.start) {
        params = params.set('startDate', filters.start ?? undefined);
        params = params.set('endDate', filters.end ?? undefined);
      }
    }
    // return this.service.post(`${flowTraceabilityEnv.flowTraceabilityContext}/flow-traceability/flow-validation/${filters?.page}/${filters?.pageSize}`, payload, params);
    return this.service.get(`${flowTraceabilityEnv.flowTraceabilityContext}/validation/flow/${filters?.page}/${filters?.pageSize}`, params);
  }


  getDocumentByFlowId(id: number, filters: { page: number, pageSize: number, sortByField: string,  sortDirection: string  }): Observable<ApproveDocResponse> {

    let params = new HttpParams();
    params = params.set('flowId', id);

    if(filters.sortByField) {
      params = params.set('sortByField', filters.sortByField);
    }

    if(filters.sortDirection) {
      params = params.set('sortDirection', filters.sortDirection);
    }

    if(filters.page) { params = params.set('page', filters.page); }
    if(filters.page) { params = params.set('pageSize', filters.pageSize); }


    return this.service.get(`${flowTraceabilityEnv.flowTraceabilityContext}/validation/document`, params);
  }


  updateValidate(flows: any[], status: StatusUtils.VALIDATED | StatusUtils.REFUSE_DOC, comment:string) {
    const payload = {
      fileIds: flows,
      status,
      comment
    }

    return this.service.put(`${flowTraceabilityEnv.flowTraceabilityContext}/validation/flow`, payload);
  }


  updateValidateDoc(docs: any[], action: StatusUtils.VALIDATED | StatusUtils.REFUSE_DOC, flowId: number, comment: string) {
    const payload = {
      documentIds: docs,
      action,
      flowId,
      comment:comment
    }

    return this.service.post(`${flowTraceabilityEnv.flowTraceabilityContext}/validation/document`, payload);
  }

  downloadBase64(fileId: string): Observable<any>{
    return this.service.base64File(`${flowTraceabilityEnv.flowTraceabilityContext}/storage/file/${fileId}`);
  }

  fetchRemainingDateShipment(): Observable<RemainingShipmentModel> {
    return this.service.get(`${flowTraceabilityEnv.flowTraceabilityContext}/validation/remaining`);
  }

  constructor(private readonly service: CxmFlowTraceabilityService) { }
}
