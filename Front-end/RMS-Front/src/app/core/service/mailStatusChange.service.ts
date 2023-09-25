import { HttpParams } from '@angular/common/http';
import { MailStatuschangelist, MailStatuschangeModel } from './../model/mailStatusChange';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MailStatusChangeService {

  constructor(private service: ApiService) { }
  getList(index?: number, size?: number, filter?: string, sortDirection?: string, sortByField?: string,
          selectType?: string): Observable<MailStatuschangelist> {
    let params = new HttpParams();
    if (size > 0) { params = params.set('pageSize', size.toString()); }
    if (index > 0) { params = params.set('page', index.toString()); }
    if (filter !== undefined) { params = params.set('filter', filter); }
    if (sortDirection && sortDirection !== '') { params = params.set('sortDirection', sortDirection); }else {
      params = params.set('sortDirection', 'desc'); }
    if (sortByField !== undefined) { params = params.set('sortByField', sortByField); }
    if (selectType !== undefined && selectType !== '') { params = params.set('selectType', selectType); }
    return this.service.get(`${environment.rmsContextPath}/mail/configuration`, params);
  }
  getById(id: number): Observable<any> {
    return this.service.get(`${environment.rmsContextPath}/mail/configuration/${id}`);
  }

  create(formData: MailStatuschangeModel): Observable<any> {
    return this.service.post(`${environment.rmsContextPath}/mail/configuration`, formData);
  }

  update(id: number, formData: MailStatuschangeModel): Observable<any> {
    return this.service.put(`${environment.rmsContextPath}/mail/configuration`, formData);
  }
  delete(id: number): Observable<any> {
    return this.service.delete(`${environment.rmsContextPath}/mail/configuration/${id}`);
  }
  restoreMailConfig(id: number, deleted: boolean): Observable<any> {
    return this.service.patch(`${environment.rmsContextPath}/mail/configuration/${id}/deleted/${deleted}`);
  }
  updateActive(id: number, active: boolean): Observable<any> {
    return this.service.patch(`${environment.rmsContextPath}/mail/configuration/${id}/active/${active}`);
  }
}
