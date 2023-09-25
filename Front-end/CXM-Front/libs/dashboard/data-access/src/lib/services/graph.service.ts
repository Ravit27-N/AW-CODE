import { Injectable } from "@angular/core";
import { ApiService } from "@cxm-smartflow/shared/data-access/api";
import { Observable } from "rxjs";
import { DashboardGraphModel, IGraphFlowTrackingResult, IGraphItem, IUpdateUserObject, UserValidation } from "../models";
import { HttpParamsBuilder } from "@cxm-smartflow/analytics/util";


@Injectable()
export class AnalysticService {

  fetchGraphChannel(requestedAt: DashboardGraphModel): Observable<any[]> {
    const param = new HttpParamsBuilder<DashboardGraphModel>()
      .set(requestedAt)
      .removeFalsyFields()
      .build();
    return this.api.get('/cxm-analytics/api/v1/flow-traceability/report/flow-channel', param);
  }

  fetchGraphDepositMode(requestedAt: DashboardGraphModel): Observable<IGraphItem[]> {
    const param = new HttpParamsBuilder<DashboardGraphModel>()
      .set(requestedAt)
      .removeFalsyFields()
      .build();
    return this.api.get('/cxm-analytics/api/v1/flow-traceability/report/deposit-modes', param);
  }

  fetchGraphFlowTracking(requestedAt: DashboardGraphModel): Observable<IGraphFlowTrackingResult> {
    const param = new HttpParamsBuilder<DashboardGraphModel>()
      .set(requestedAt)
      .removeFalsyFields()
      .build();
    return this.api.get('/cxm-analytics/api/v1/flow-traceability/report/flow-documents', param);
  }

  fetchGraphEvolution(requestedAt: DashboardGraphModel): Observable<any[]> {
    const param = new HttpParamsBuilder<DashboardGraphModel>()
      .set(requestedAt)
      .removeFalsyFields()
      .build();
    return this.api.get('/cxm-analytics/api/v1/flow-traceability/report/evolution', param)
  }

  updateUserFilter(param: IUpdateUserObject): Observable<IUpdateUserObject> {
    return this.api.post('/cxm-analytics/api/v1/preference/filter', param);
  }

  fetchUserFilter(): Observable<IUpdateUserObject> {
    return this.api.get('/cxm-analytics/api/v1/preference/filter')
  }

  fetchValidationDocumentExists(): Observable<UserValidation> {
    return this.api.get('/cxm-flow-traceability/api/v1/validation/remaining');
  }

  constructor(private api: ApiService) { }
}
