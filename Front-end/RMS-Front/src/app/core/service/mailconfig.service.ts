import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SystemConfiguration, SystemConfigurationList } from '../model/MailconfigFormModel';
import { ApiService } from './api.service';
import { FilterOptions } from './interview.service';
import {environment} from '../../../environments/environment';

@Injectable()
export class MailconfigService {

  constructor(private apiService: ApiService) { }

  saveConfig(config: SystemConfiguration): Observable<any> {
    return this.apiService.put(`/${environment.rmsContextPath}/systemConfig`, config);
  }

  getConfigList(pageNumber?: number, pageSize?: number, filter?: FilterOptions): Observable<SystemConfigurationList> {
    let params = new HttpParams();
    if (pageSize !== undefined) { params = params.set('pageSize', pageSize.toString()); }
    if (pageNumber !== undefined) { params = params.set('page', pageNumber.toString()); }

    if (filter) {
      Object.keys(filter).forEach((k) => params = params.set(k, filter[k]));
    }

    return this.apiService.get(`${environment.rmsContextPath}/systemConfig`, params);
  }

  deleteConfig(id: number): Observable<any> {
    return this.apiService.delete(`${environment.rmsContextPath}/systemConfig/${id}`);
  }
}
