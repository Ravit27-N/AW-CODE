import {ReminderList, ReminderModel, ReminderFormModel, DashboardReportReminderModel} from '../model';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {EntityResponseHandler} from "../../shared";
@Injectable()
export class ReminderService {

  constructor(
    private apiService: ApiService
  ) { }

  getList(
    page?: number, pageSize?: number, reminderTypes?: string[],
    filter?: string | number | Date, sortByField?: string, sortDirection?: string,
    startDate?: Date| string, endDate?: Date| string): Observable<ReminderList> {
    let params = new HttpParams();
    if (page !== undefined) { params = params.set('page', page.toString()); }
    if (pageSize !== undefined) { params = params.set('pageSize', pageSize.toString()); }
    if (reminderTypes !== undefined) { params = params.set('reminderTypes', reminderTypes.toString()); }
    if (filter !== undefined) { params = params.set('filter', filter.toString()); }
    if (sortByField !== undefined) { params = params.set('sortByField', sortByField.toString()); }
    if (sortDirection !== undefined) { params = params.set('sortDirection', sortDirection.toString()); }
    if (startDate !== undefined) { params = params.set('startDate', startDate.toString()); }
    if (endDate !== undefined) { params = params.set('endDate', endDate.toString()); }
    return this.apiService.get(`${environment.rmsContextPath}/reminder`, params);
  }

  getReminderById(id: number): Observable<ReminderModel> {
    return this.apiService.get(`${environment.rmsContextPath}/reminder/${id}`);
  }

  delete(id: number): Observable<any> {
    return this.apiService.post(`${environment.rmsContextPath}/reminder/${id}/hardDelete`);
  }

  changeStatus(id: number | string, active: boolean): Observable<any> {
    return this.apiService.patch(`${environment.rmsContextPath}/reminder/${id}/status/${active}`);
  }

  updateReminder(id: number, formData: ReminderFormModel): Observable<any> {
    return this.apiService.patch(`${environment.rmsContextPath}/reminder/${id}`, formData);
  }

  create(formData: ReminderFormModel): Observable<{ reminderId: string }> {
    return this.apiService.post(`${environment.rmsContextPath}/reminder`, formData);
  }


  getDashboardList(page?: number, pageSize?: number, sortDirection?: string, sortByField?: string
                 , startDate?: string , endDate?: string, active?: boolean): Observable<EntityResponseHandler<DashboardReportReminderModel>>
  {
    let params = new HttpParams();
    if (page !== undefined) { params = params.set('page', page.toString()); }
    if (pageSize !== undefined) { params = params.set('pageSize', pageSize.toString()); }
    if (sortDirection !== undefined) { params = params.set('sortDirection', sortDirection.toString()); }
    if (sortByField !== undefined) { params = params.set('sortByField', sortByField.toString()); }
    if (startDate !== undefined) {params = params.set('startDate', startDate.toString()); }
    if (endDate !== undefined) {params = params.set('endDate', endDate.toString()); }
    if(active) {
      params = params.set('active', active.toString());
    }
    params = params.set('reminderTypes', '');
    return this.apiService.get(`${environment.rmsContextPath}/reminder`, params);
  }
}
