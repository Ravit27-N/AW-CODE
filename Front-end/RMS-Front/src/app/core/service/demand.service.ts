import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { DemandDetailModel, DemandModel, ProjectDetailModel } from '../model';
import { HttpParams } from '@angular/common/http';
import { FilterOptions } from './interview.service';
import { EntityResponseHandler } from '../../shared';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DemandService {
  constructor(private apiService: ApiService) {}

  getList(
    httpParams: HttpParams,
  ): Observable<EntityResponseHandler<DemandModel>> {
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
    return this.apiService.patch(`${environment.rmsContextPath}/demand/${demandId}/candidates/${ids}`);
  }

  addResourceToDemand(body: {
    demandId: number;
    candidateIds: number[];
  }): Observable<any> {
    return this.apiService.put(`${environment.rmsContextPath}/demand/add-candidates`, body);
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
    return this.apiService.post(`${environment.rmsContextPath}/demand`, formData);
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

  softDelete(id): Observable<any> {
    return this.apiService.patch(`${environment.rmsContextPath}/demand/${id}/delete/true`);
  }

  getById(demandId: number | string): Observable<DemandModel> {
    return this.apiService.get(`${environment.rmsContextPath}/demand/${demandId}`);
  }

  getDemandById(demandId: number | string): Observable<DemandDetailModel> {
    return this.apiService.get(`${environment.rmsContextPath}/demand/${demandId}`);
  }

  getDemandDetailsById(
    projectId: string | number,
  ): Observable<ProjectDetailModel> {
    return this.apiService.get(`${environment.rmsContextPath}/demand/detail/${projectId}`);
  }

  update(id: number | string, formData: DemandModel): Observable<any> {
    return this.apiService.put(`${environment.rmsContextPath}/demand`, formData);
  }

  updateStatus(id: number | string, status: boolean | string): Observable<any> {
    return this.apiService.patch(`${environment.rmsContextPath}/demand/${id}/active/${status}`);
  }

  restore(id?: number | string, isDelete?: boolean): Observable<any> {
    return this.apiService.patch(`${environment.rmsContextPath}/demand/${id}/delete/${isDelete}`);
  }

  hardDelete(id?: number): Observable<any> {
    return this.apiService.delete(`${environment.rmsContextPath}/demand/${id}`);
  }
}
