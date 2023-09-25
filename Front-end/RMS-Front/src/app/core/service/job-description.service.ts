import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { IEnvelope } from '../model';
import { ApiService } from './api.service';
import {environment} from '../../../environments/environment';

export interface JobDescription {
  id: number;
  title: string;
  description: string;
  filename: string;
  active?: boolean;
}


@Injectable()
export class JobDescriptionService {

  constructor(
    private service: ApiService
  ) { }

  get(page: number, pageSize: number, filter?: string): Observable<IEnvelope<JobDescription>> {
    let params = new HttpParams();
    if (pageSize !== undefined) { params = params.set('pageSize', pageSize.toString()); }
    if (page !== undefined) { params = params.set('page', page.toString()); }
    if (filter !== undefined) { params = params.set('filter', filter.toString()); }

    return this.service.get(`${environment.rmsContextPath}/jobDescription`, params);
  }

  create(job: JobDescription): Observable<any> {
    return this.service.post(`${environment.rmsContextPath}/jobDescription`, job);
  }
}
