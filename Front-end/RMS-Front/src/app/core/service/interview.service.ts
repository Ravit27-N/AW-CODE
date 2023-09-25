import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ApiService} from './api.service';
import {
  DashboardReportInterviewModel,
  Interview,
  InterviewFormModel,
  InterviewList,
  InterviewResult,
  InterviewResultForm
} from '../model';
import {map} from 'rxjs/operators';
import {HttpParams} from '@angular/common/http';
import {CandidateOnDemandList} from '../model';
import {environment} from '../../../environments/environment';
import {EntityResponseHandler} from "../../shared";

export interface FilterOptions {
  filter?: string;
  sortDirection?: string;
  sortByField?: string;
  startDate?: string;
  endDate?: string;
  status?: string[];
}

@Injectable()
export class InterviewService {

  constructor(private api: ApiService) { }

  getById(id: number | string): Observable<Interview> {
    return this.api.get(`${environment.rmsContextPath}/interview/${id}`);
  }

  // Today list is a interview list for this week
  getTodayList(size?: number, index?: number, date? : Date | string): Observable<InterviewList>{
    let params = new HttpParams();
    if (size !== undefined) { params = params.set('pageSize', size.toString()); }
    if (index !== undefined) { params = params.set('page', index.toString()); }
    if ( date !== undefined) { params = params.set('startDate', date.toString()); }
    if ( date !== undefined) { params = params.set('endDate', date.toString()); }
    return this.api.get(`${environment.rmsContextPath}/interview`, params);
  }

  getAllCandidateIds(page?: number, pageSize?: number): Observable<CandidateOnDemandList>{
    let params=new HttpParams();
    if (page !== undefined) { params = params.set('page', page.toString()); }
    if (pageSize !== undefined) { params = params.set('pageSize', pageSize.toString()); }
    return this.api.get(`${environment.rmsContextPath}/candidate/candidateId`,params);
  }

  getList(size?: number, index?: number, filter?: FilterOptions | string): Observable<EntityResponseHandler<DashboardReportInterviewModel>> {
    let params = new HttpParams();
    if (size !== undefined) { params = params.set('pageSize', size.toString()); }
    if (index !== undefined) { params = params.set('page', index.toString()); }

    if (filter) {
      Object.keys(filter).forEach((k) => params = params.set(k, filter[k]));
    }

    return this.api.get(`${environment.rmsContextPath}/interview`, params);
  }
  getInterviewList(page?: number, pageSize?: number, filter?: string): Observable<InterviewList> {
    let params = new HttpParams();
    if (pageSize !== undefined) { params = params.set('pageSize', pageSize.toString()); }
    if (page !== undefined) { params = params.set('page', page.toString()); }
    if (filter !== undefined) { params = params.set('filter', filter.toString()); }
    return this.api.get(`${environment.rmsContextPath}/interview`, params);
  }

  create(formData: InterviewFormModel): Observable<{ id: number }> {
    return this.api.post(`${environment.rmsContextPath}/interview`, formData);
  }

  update(interview: Interview, formData: InterviewFormModel): Observable<any> {
    return this.api.patch(`${environment.rmsContextPath}/interview/${interview.id}`, formData);
  }

  getResult(interview: Interview): Observable<InterviewResult> {
    return this.api.get(`${environment.rmsContextPath}/interview/${interview.id}/result`).pipe(map(this.transformResult));
  }

  private transformResult(value: InterviewResult): InterviewResult {
    const scoreData = JSON.parse(value.score);
    const newValue = { ...value };
    newValue.score = scoreData;
    return newValue;
  }

  updateResult(interview: Interview, formData: InterviewResultForm): Observable<any> {
    // Note: model form and request model are difference
    const payload: InterviewResult = {
      english: formData.english,
      logical: formData.logical,
      oral: formData.qa,
      flexibility: formData.flexibily,
      average: formData.avarage,
      remark: formData.remark,
      score: JSON.stringify({
        quiz: { score: formData.quizScore, max: formData.quizMax },
        coding: { score: formData.codeScore, max: formData.codeMax }
      })
    };

    return this.api.put(`${environment.rmsContextPath}/interview/${interview.id}/result`, payload);
  }

  changeStatus(interview: Interview, statusId: number): Observable<any> {
    return this.api.patch(`${environment.rmsContextPath}/interview/${interview.id}/status/${statusId}`);
  }

  softDelete(interview: Interview, deleted: boolean): Observable<any> {
    return this.api.patch(`${environment.rmsContextPath}/interview/${interview.id}/delete/${deleted}`, {});
  }

  delete(interview: Interview): Observable<any> {
    return this.api.delete(`${environment.rmsContextPath}/interview/${interview.id}`);
  }

}
