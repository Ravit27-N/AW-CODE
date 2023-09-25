import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  EmployeeInterviewTemplateModel,
  InterviewTemplateList,
  InterviewTemplateModel,
} from '../model';
import { ApiService } from './api.service';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

@Injectable()
export class InterviewTemplateService {
  constructor(private apiService: ApiService) {}

  getById(id: number): Observable<InterviewTemplateModel> {
    return this.apiService.get(
      `${environment.rmsContextPath}/interview/status/${id}`,
    );
  }

  getList(
    index?: number,
    size?: number,
    sortByField?: string,
    sortDirection?: string,
    filter?: string,
  ): Observable<InterviewTemplateList> {
    let params = new HttpParams();
    if (size > 0) {
      params = params.set('pageSize', size.toString());
    }
    if (index > 0) {
      params = params.set('page', index.toString());
    }
    if (sortByField !== undefined) {
      params = params.set('sortByField', sortByField);
    }
    if (sortDirection && sortDirection !== '') {
      params = params.set('sortDirection', sortDirection);
    }
    if (filter !== undefined) {
      params = params.set('filter', filter);
    }
    return this.apiService.get(
      `${environment.rmsContextPath}/interview/status`,
      params,
    );
  }

  create(formData: InterviewTemplateModel): Observable<any> {
    return this.apiService.post(
      `${environment.rmsContextPath}/interview/status`,
      formData,
    );
  }

  changeStatus(id: number, active: boolean): Observable<any> {
    return this.apiService.patch(
      `${environment.rmsContextPath}/interview/status/${id}/active/${active}`,
      { active },
    );
  }

  update(id: number, formData: InterviewTemplateModel): Observable<any> {
    return this.apiService.patch(
      `${environment.rmsContextPath}/interview/status`,
      formData,
    );
  }

  delete(id: number): Observable<any> {
    return this.apiService.delete(
      `${environment.rmsContextPath}/interview/status/${id}`,
    );
  }

  getEmployees(
    index?: number,
    size?: number,
  ): Observable<EmployeeInterviewTemplateModel> {
    let params = new HttpParams();
    params = params.set('pageSize', size > 0 ? size.toString() : '100');
    params = params.set('page', index > 0 ? index.toString() : '1');
    return this.apiService.get(
      `${environment.rmsContextPath}/employee`,
      params,
    );
  }

  validateInterviewTemplate(name?: string, id?: number): Observable<boolean> {
    let params = new HttpParams();
    if (id) {
      params = params.set('id', id.toString());
    }
    params = params.set('name', name);
    return this.apiService.get(
      `${environment.rmsContextPath}/interview/status/exist`,
      params,
    );
  }
}
