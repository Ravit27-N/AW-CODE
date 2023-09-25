import { Injectable } from '@angular/core';
import {ApiService, CxmAnalyticsService} from '@cxm-smartflow/shared/data-access/api';
import {
  AnalyticsDistributionCriteria, DistributionByStatus,
  FilterCriteriaModel,
  FilterListModel,
  FilterOptionParam,
  GlobalProductionDetailsModel,
  PreferenceDistributionGraphModel,
  PreferenceGraphModel,
  ProductionDetails,
  ReportingPostalParams,
} from '../models';
import { Observable, of } from 'rxjs';
import { flowTraceabilityEnv } from '@env-flow-traceability';
import { cxmProfileEnv } from '@env-cxm-profile';
import { cxmAnalyticsEnv } from '@env-cxm-analytics';
import { settingEnv } from '@env-cxm-setting';
import {HttpClient, HttpHeaders, HttpParams, HttpResponse} from '@angular/common/http';
import { HttpParamsBuilder } from '@cxm-smartflow/analytics/util';
import {appRoute} from "@cxm-smartflow/shared/data-access/model";
import {DistributionByStatusUtil} from "../../../../util/src/lib/distribution-by-status";
import {campaignEnv as env} from "@env-cxm-campaign";

@Injectable({ providedIn: 'root' })
export class ManageAnalyticsService {
  constructor(
    private _analyticsService: CxmAnalyticsService,
    private http: HttpClient,
    private apiService: ApiService,
  ) {}

  /**
   * Get filter criteria.
   */
  getFilterCriteria(): Observable<FilterCriteriaModel> {
    return this._analyticsService.get(`${flowTraceabilityEnv.flowTraceabilityContext}/flow-traceability/filter-criteria`);
  }

  /**
   * Get a list of fillers.
   */
  getFillerList(): Observable<FilterListModel[]> {
    const params = new HttpParams()
      .set('resolve-value', 'true');
    return this._analyticsService.get(`${cxmProfileEnv.profileContext}/clients/client-fillers`, params);
  }

  fetchDistributionCriteria(): Observable<AnalyticsDistributionCriteria> {
    return this._analyticsService.get(`${settingEnv.settingContext}/setting/criteria-distribution`);
  }

  fetchVolumeReceived(filterOption: FilterOptionParam): Observable<PreferenceGraphModel[]> {
    const params = new HttpParamsBuilder<FilterOptionParam>()
      .set(filterOption)
      .removeFalsyFields()
      .build();

    return this._analyticsService.get(`${cxmAnalyticsEnv.analyticsContext}/statistic/global/volume-received`, params);
  }

  fetchGlobalProductionDetails(filterOption: FilterOptionParam): Observable<GlobalProductionDetailsModel> {
    const params = new HttpParamsBuilder<FilterOptionParam>()
      .set(filterOption)
      .removeFalsyFields()
      .build();

    return this._analyticsService.get(`${cxmAnalyticsEnv.analyticsContext}/statistic/global/production-details`, params);

  }

  fetchGlobalProductionProgress(filterOption: FilterOptionParam): Observable<PreferenceGraphModel[]> {
    const params = new HttpParamsBuilder<FilterOptionParam>()
      .set(filterOption)
      .removeFalsyFields()
      .build();

    return this._analyticsService.get(`${cxmAnalyticsEnv.analyticsContext}/statistic/global/production-progress`, params);
  }

  fetchDistributionVolumeReceived(reportingPostalParams: ReportingPostalParams): Observable<PreferenceDistributionGraphModel[]> {
    const params = new HttpParamsBuilder<ReportingPostalParams>()
      .set(reportingPostalParams)
      .removeFalsyFields()
      .build();
    return this._analyticsService.get(`${cxmAnalyticsEnv.analyticsContext}/statistic/distribution-volume-received`, params);
  }

  fetchProductionDetails(filterOption: ReportingPostalParams): Observable<ProductionDetails> {
    const params = new HttpParamsBuilder<ReportingPostalParams>()
      .set(filterOption)
      .removeFalsyFields()
      .build();

     return this._analyticsService.get(`${cxmAnalyticsEnv.analyticsContext}/statistic/production-details`, params);
  }

  fectDistributionPND(reportingPostalParams: ReportingPostalParams): Observable<PreferenceDistributionGraphModel[]> {
    const params = new HttpParamsBuilder<ReportingPostalParams>()
      .set(reportingPostalParams)
      .removeFalsyFields()
      .build();
    return this._analyticsService.get(`${cxmAnalyticsEnv.analyticsContext}/statistic/postal/none-distribution-by-status`, params);
  }

  fetchProcessedMail(reportingPostalParams: ReportingPostalParams): Observable<PreferenceDistributionGraphModel[]> {
    const params = new HttpParamsBuilder<ReportingPostalParams>()
      .set(reportingPostalParams)
      .removeFalsyFields()
      .build();
    return this._analyticsService.get(`${cxmAnalyticsEnv.analyticsContext}/statistic/production-delivered`, params);
  }

  fetchDistributionByStatus(reportingPostalParams: ReportingPostalParams): Observable<DistributionByStatus[]> {
    const params = new HttpParamsBuilder<ReportingPostalParams>()
      .set(reportingPostalParams)
      .removeFalsyFields()
      .build();
    return this._analyticsService.get(`${cxmAnalyticsEnv.analyticsContext}/statistic/digital/distribution-by-status`, params);
  }

  exportCsvFile(reportingPostalParams: ReportingPostalParams) {
    const params = new HttpParamsBuilder<ReportingPostalParams>()
      .set(reportingPostalParams)
      .removeFalsyFields()
      .build();
    return this.getFile(`${cxmAnalyticsEnv.analyticsContext}/statistic/export`, params);
  }

  getFile(path?: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.apiService.getFileWithFileName(path, params);
  }
}
