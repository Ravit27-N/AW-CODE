import { Injectable } from '@angular/core';
import {
  ApiService,
  DemandDetailModel,
  DemandModel,
  FilterOptions,
  ProjectDetailModel,
} from '../../../core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EntityResponseHandler } from '../../../shared';
import { environment } from '../../../../environments/environment';
import { ScopedDemandListItem } from '../models/scoped-model-demand-list.model';
import {ScopedModelDemandDetails} from "../models/scoped-model-demand-details.model";

@Injectable({
  providedIn: 'root',
})
export class ScopedServiceDemandService {
  constructor(private apiService: ApiService) {}

  getAllDemands(
    httpParams: HttpParams,
  ): Observable<EntityResponseHandler<ScopedDemandListItem>> {
    return this.apiService.get(`${environment.rmsContextPath}/demand`, httpParams);
  }

  getDemandOnDashboard(
    index?: number,
    size?: number,
    active?: boolean,
    filter?: FilterOptions,
  ): Observable<any> {
    let params = new HttpParams();
    if (index !== undefined) {
      params = params.set('page', index.toString());
    }
    if (size !== undefined) {
      params = params.set('pageSize', size.toString());
    }
    if (active !== undefined) {
      params = params.set('active', String(active));
    }
    if (filter) {
      Object.keys(filter).forEach((k) => (params = params.set(k, filter[k])));
    }
    return this.apiService.get(`${environment.rmsContextPath}/demand`, params);
  }

  addCandidatesToDemand(
    demandId: string | number,
    ids: string,
  ): Observable<any> {
    return this.apiService.patch(
      `${environment.rmsContextPath}/demand/${demandId}/candidates/${ids}`,
    );
  }

  addResourceToDemand(body: {
    demandId: number;
    candidateIds: number[];
  }): Observable<any> {
    return this.apiService.put(
      `${environment.rmsContextPath}/demand/add-candidates`,
      body,
    );
  }

  removeCandidateFromDemand(
    demandId: string | number,
    candidateId: string | number,
  ): Observable<any> {
    return this.apiService.patch(
      `${environment.rmsContextPath}/demand/${demandId}/delete-candidates/${candidateId}`,
    );
  }

  create(formData: DemandModel): Observable<any> {
    return this.apiService.post(
      `${environment.rmsContextPath}/demand`,
      formData,
    );
  }

  validateProjectJob(
    projectId: string | number,
    jobDescriptionId: string | number,
  ): Observable<any> {
    return this.apiService.get(
      `${environment.rmsContextPath}/demand/validateProject/${projectId}/validateJob/${jobDescriptionId}`,
    );
  }

  validateUpdateProjectJob(
    projectId: string | number,
    jobDescriptionId: string | number,
    demandId: number,
  ): Observable<any> {
    return this.apiService.get(
      `${environment.rmsContextPath}/demand/${demandId}/validateProject/${projectId}/validateJob/${jobDescriptionId}`,
    );
  }

  moveDemandFromListToArchive(id: number): Promise<void> {
    return this.apiService.patch(`${environment.rmsContextPath}/demand/${id}/delete/true`).toPromise();
  }

  getDemandById(demandId: number): Promise<ScopedModelDemandDetails> {
    return this.apiService.get(`${environment.rmsContextPath}/demand/${demandId}`).toPromise();
  }

  getDemandByProjectId(
    projectId: number,
  ): Observable<ProjectDetailModel> {
    return this.apiService.get(`${environment.rmsContextPath}/demand/detail/${projectId}`,);
  }

  updateDemand(formData: DemandModel): Promise<void> {
    return this.apiService.put(`${environment.rmsContextPath}/demand`, formData).toPromise();
  }

  pinOrUnpinDemandOnDashboard(id: number, status: boolean): Promise<void> {
    return this.apiService.patch(`${environment.rmsContextPath}/demand/${id}/active/${status}`).toPromise();
  }

  restoreDemandFromArchiveToList(id: number, isDelete: boolean): Promise<void> {
    return this.apiService.patch(`${environment.rmsContextPath}/demand/${id}/delete/${isDelete}`).toPromise();
  }

  deleteDemandPermanent(id: number): Promise<void> {
    return this.apiService.delete(`${environment.rmsContextPath}/demand/${id}`).toPromise();
  }
}
