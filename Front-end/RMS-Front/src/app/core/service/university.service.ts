import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UniversityModel, UniversityList } from '../model';
import { ApiService } from './api.service';
import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';

@Injectable()
export class UniversityService {

  constructor(private apiService: ApiService) { }

  getById(id: number): Observable<UniversityModel> {
    return this.apiService.get(`${environment.rmsContextPath}/university${id}`);
  }

  getList(page?: number, pageSize?: number, filter?: string | number | Date,
          sortDirection?: string, sortByField?: string ): Observable<UniversityList> {
    let params = new HttpParams();
    if (page !== undefined) { params = params.set('page', page.toString()); }
    if (pageSize !== undefined) { params = params.set('pageSize', pageSize.toString()); }
    if (filter !== undefined) { params = params.set('filter', filter.toString()); }
    if (sortDirection !== undefined) { params = params.set('sortDirection', sortDirection.toString()); }
    if (sortByField !== undefined) { params = params.set('sortByField', sortByField.toString()); }
    return this.apiService.get(`${environment.rmsContextPath}/university`, params);
  }

  create(formData: UniversityModel): Observable<any> {
    return this.apiService.post(`${environment.rmsContextPath}/university`, formData);
  }
  update(id: number, formData: UniversityModel): Observable<any> {
    return this.apiService.put(`${environment.rmsContextPath}/university`, formData);
  }
  delete(id: number): Observable<any> {
    return this.apiService.delete(`${environment.rmsContextPath}/university${id}`);
  }
}
