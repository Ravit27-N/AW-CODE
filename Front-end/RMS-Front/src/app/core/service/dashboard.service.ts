import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { DashboardQuickAccessModel, DashboardTopCandidateModel } from '../model';
import { EntityResponseHandler } from '../../shared';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  constructor(private service: ApiService) {}

  getInterviewGraph(year?: number): Observable<any> {
    let params = new HttpParams();

    if (year) {
      params = params.set('year', year?.toString());
    }

    return this.service.get(`${environment.rmsContextPath}/dashboard/interview/graph`, params);
  }

  getDashboardCounts(): Observable<DashboardQuickAccessModel> {
    return this.service.get(`${environment.rmsContextPath}/dashboard/count`);
  }

  getTopCandidates(
    httpParams: HttpParams,
  ): Observable<EntityResponseHandler<DashboardTopCandidateModel>> {
    return this.service.get(`${environment.rmsContextPath}/dashboard/candidate/top`, httpParams);
  }

  getCandidateCount(): Observable<any> {
    return this.service.get(`${environment.rmsContextPath}/dashboard/candidate/count`);
  }
}
