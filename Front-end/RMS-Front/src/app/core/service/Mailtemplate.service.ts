import {MailTemplateBodyModel, MailTemplateList, MailTemplateModel} from './../model/Mailtemplate';

import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import {environment} from '../../../environments/environment';
import {KeyValue} from "@angular/common";

@Injectable()
export class MailtemplateService {

  constructor(private apiService: ApiService) { }
  getList(index?: number, size?: number, filter?: string, sortDirection?: string, sortByField?: string,
          selectType?: string): Observable<MailTemplateList> {
    let params = new HttpParams();
    if (size > 0) { params = params.set('pageSize', size.toString()); }
    if (index > 0) { params = params.set('page', index.toString()); }
    if (filter !== undefined) { params = params.set('filter', filter); }
    if (sortDirection && sortDirection !== '') { params = params.set('sortDirection', sortDirection); } else {
      params = params.set('sortDirection', 'desc'); }
    if (sortByField !== undefined) { params = params.set('sortByField', sortByField); }
    if (selectType !== undefined) { params = params.set('selectType', selectType); }
    return this.apiService.get(`${environment.rmsContextPath}/mail/template`, params);
  }
  getListOnActive(index?: number, size?: number): Observable<MailTemplateList> {
  let params = new HttpParams();
  if (size > 0) { params = params.set('pageSize', size.toString()); }
  if (index > 0) { params = params.set('page', index.toString()); }
  params = params.set('selectType', 'active');
  return this.apiService.get(`${environment.rmsContextPath}/mail/template`, params);
  }
  getById(id: number): Observable<MailTemplateModel>{
    return this.apiService.get(`${environment.rmsContextPath}/mail/template/${id}`);
  }
  create(formData: MailTemplateModel): Observable<any>{
    return this.apiService.post(`${environment.rmsContextPath}/mail/template`, formData);
  }
  update(id: number, formData: MailTemplateModel): Observable<any>{
    return this.apiService.put(`${environment.rmsContextPath}/mail/template`, formData);
  }
  delete(id: number): Observable<any> {
    return this.apiService.delete(`${environment.rmsContextPath}/mail/template/${id}`);
  }
  updateableDelete(id: number, isDeleteable: boolean): Observable<any>
  {
    return this.apiService.patch(`${environment.rmsContextPath}/mail/template/${id}/isAbleDelete/${isDeleteable}`);
  }
  updateDeleted(id: number, deleted: boolean): Observable<any>
  {
    return this.apiService.patch(`${environment.rmsContextPath}/mail/template/${id}/deleted/${deleted}`);
  }
  updateActive(id: number, active: boolean): Observable<any>
  {
    return this.apiService.patch(`${environment.rmsContextPath}/mail/template/${id}/active/${active}`);
  }

  getMailTemplateBody(): Observable<KeyValue<string, any>[]>{
    return this.apiService.get(`${environment.rmsContextPath}/mail/template/mail/template-body`);
  }
}
