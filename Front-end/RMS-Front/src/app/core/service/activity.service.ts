import { ActivityModel } from 'src/app/core';
import { ActivityList, ActivityFormModel } from '../model';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import {environment} from '../../../environments/environment';

@Injectable()
export class ActivityService {
  constructor(
    private apiService: ApiService
  ) { }

  getList(page?: number, pageSize?: number, filter?: string|number|Date,
    sortDirection?: string, sortByField?: string): Observable<ActivityList> {
    let params = new HttpParams();
    if (page !== undefined) { params = params.set('page', page.toString()); }
    if (pageSize !== undefined) { params = params.set('pageSize', pageSize.toString()); }
    if (filter !== undefined) { params = params.set('filter', filter.toString()); }
    if (sortDirection !== undefined) { params = params.set('sortDirection', sortDirection.toString()); }
    if (sortByField !== undefined) { params = params.set('sortByField', sortByField.toString()); }
    return this.apiService.get(`${environment.rmsContextPath}/activity`, params);
  }

  getById(id: number | string): Observable<ActivityModel>{
     return this.apiService.get(`${environment.rmsContextPath}/activity/${id}`);
  }

  create(formData: ActivityFormModel): Observable<{ activityId: number }> {
    return this.apiService.post(`${environment.rmsContextPath}/activity`, formData);
  }

  update(formData: ActivityFormModel, id: number | string): Observable<any> {
    return this.apiService.patch(`${environment.rmsContextPath}/activity/${id}`, formData);
  }


}
