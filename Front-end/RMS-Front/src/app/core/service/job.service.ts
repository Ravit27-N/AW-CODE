import { HttpParams } from '@angular/common/http';
import { JobList, JobModel } from '../model';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class JobService {
  constructor(private apiservice: ApiService) {}

  getlist(
    index?: number,
    size?: number,
    filter?: string,
    sortDirection?: string,
    sortByField?: string,
  ): Observable<JobList> {
    let params = new HttpParams();
    if (size > 0) {
      params = params.set('pageSize', size.toString());
    }
    if (index > 0) {
      params = params.set('page', index.toString());
    }
    if (status) {
      params = params.set('status', status);
    }
    if (sortByField) {
      params = params.set('sortByField', sortByField);
    }
    if (sortDirection !== '') {
      params = params.set('sortDirection', sortDirection);
    } else {
      params = params.set('sortDirection', 'desc');
    }
    if (filter) {
      params = params.set('filter', filter);
    }
    return this.apiservice.get(
      `${environment.rmsContextPath}/jobDescription`,
      params,
    );
  }

  getById(statusId: number): Observable<JobModel> {
    return this.apiservice.get(
      `${environment.rmsContextPath}/jobDescription/${statusId}`,
    );
  }

  create(formData: JobModel): Observable<JobModel> {
    return this.apiservice.post(
      `${environment.rmsContextPath}/jobDescription`,
      formData,
    );
  }

  update(id: number, formData: JobModel): Observable<any> {
    return this.apiservice.put(
      `${environment.rmsContextPath}/jobDescription`,
      formData,
    );
  }

  changeStatus(id: number, active: boolean): Observable<any> {
    return this.apiservice.patch(
      `${environment.rmsContextPath}/jobDescription/${id}/active/${active}`,
      { active },
    );
  }

  delete(id: number): Observable<any> {
    return this.apiservice.delete(
      `${environment.rmsContextPath}/jobDescription/${id}`,
    );
  }

  fileUpload(file): Observable<any> {
    return this.apiservice.upload(
      `${environment.rmsContextPath}/jobDescription/file/upload`,
      file,
    );
  }

  fileView(id: number, file: string): Observable<any> {
    return this.apiservice.viewFile(
      `${environment.rmsContextPath}/jobDescription/${id}/view/${file}`,
    );
  }

  fileRemove(id: number, file: string): Observable<any> {
    return this.apiservice.delete(
      `${environment.rmsContextPath}/jobDescription/${id}/remove/${file}`,
    );
  }

  fileUpdate(id: number, file): Observable<any> {
    return this.apiservice.updateFiles(
      `${environment.rmsContextPath}/jobDescription/${id}/profile/onUpdate`,
      file,
    );
  }
}
