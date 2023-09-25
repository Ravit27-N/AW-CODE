import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { StatusCandidateModel, StatusCandidateList } from '../model/statuscandidate';
import {environment} from "../../../environments/environment";
@Injectable()
export class StatusCandidateService {

  constructor(private apiservice: ApiService) { }

  getList(index?: number, size?: number, status?: string, sortByField?: string,
          sortDirection?: string, filter?: string): Observable<StatusCandidateList> {
    let params = new HttpParams();
    if (size > 0) { params = params.set('pageSize', size.toString()); }
    if (index > 0) { params = params.set('page', index.toString()); }
    if (status) { params = params.set('status', status); }
    if (sortByField) { params = params.set('sortByField', sortByField); }
    if (sortDirection && sortDirection !== '') { params = params.set('sortDirection', sortDirection); }
    if (filter) { params = params.set('filter', filter); }
    return this.apiservice.get(`${environment.rmsContextPath}/candidate/status`, params);
  }
  getListOnActive(index?: number, size?: number): Observable<StatusCandidateList>
  {
    let params = new HttpParams();
    if (size > 0) { params = params.set('pageSize', size.toString()); }
    if (index > 0) { params = params.set('page', index.toString()); }
    if (status) { params = params.set('status', 'active'); }
    return this.apiservice.get(`${environment.rmsContextPath}/candidate/status`, params);
  }
  getListStatusMailNotUsed({filter, sortByField, sortDirection}): Observable<any> {
    let params = new HttpParams();
    if (filter) { params = params.set('filter', filter); }
    if (sortByField) { params = params.set('sortByField', sortByField); }
    if (sortDirection && sortDirection !== '') { params = params.set('sortDirection', sortDirection); }
    return this.apiservice.get(`${environment.rmsContextPath}/candidate/status/mailConfigurationNotUsed`, params);
  }
  getById(statusId: number): Observable<StatusCandidateModel> {
    return this.apiservice.get(`${environment.rmsContextPath}/candidate/status/${statusId}`);
  }

  create(formData: StatusCandidateModel): Observable<any> {
    return this.apiservice.post(`${environment.rmsContextPath}/candidate/status`, formData);
  }

  update(id: number, formData: StatusCandidateModel): Observable<any> {
    return this.apiservice.put(`${environment.rmsContextPath}/candidate/status`, formData);
  }

  changeStatus(id: number, active: boolean): Observable<any> {
    return this.apiservice.patch(`${environment.rmsContextPath}/candidate/status/${id}/active/${active}`, { active });
  }

  softDelete(id: number, deleted: boolean): Observable<any> {
    return this.apiservice.patch(`${environment.rmsContextPath}/candidate/status/${id}/delete/${deleted}`);
  }
}
