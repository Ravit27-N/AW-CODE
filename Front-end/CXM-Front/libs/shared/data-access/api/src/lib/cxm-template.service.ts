import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { templateEnv as env } from '@env-cxm-template';
import { Params } from '@cxm-smartflow/shared/data-access/model';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root',
})
export class CxmTemplateService {
  // private baseURL = env.apiURL;

  constructor(private apiService: ApiService) {}

  getList(
    path?: string,
    page?: number,
    pageSize?: number,
    params?: Params
  ): Observable<any> {
    let params$ = new HttpParams();
    if (params?.sortByField != undefined) {
      params$ = params$.set('sortByField', params.sortByField.toString());
    }
    if (params?.sortDirection !== undefined) {
      params$ = params$.set('sortDirection', params.sortDirection.toString());
    }
    if (params?.filter !== undefined) {
      params$ = params$.set('filter', params.filter.toString());
    }
    return this.apiService.get(
      `${path}/${page}/${pageSize}`,
      params$
    );
  }

  get(path?: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.apiService.get(path, params);
  }

  post(
    path: string,
    data?: any,
    params: HttpParams = new HttpParams()
  ): Observable<any> {
    return this.apiService.post(path, data, params);
  }

  put(path: string, data?: any,  params: HttpParams = new HttpParams()): Observable<any> {
    return this.apiService.put(path, data, params);
  }

  patch(path: string, data?: any): Observable<any> {
    return this.apiService.patch(path, data);
  }

  delete(path: string, data?: any): Observable<any> {
    return this.apiService.delete(path);
  }

  getById(path?: string): Observable<any> {
    return this.apiService.get(path);
  }

  upload(
    path: string,
    data: FormData,
    params: HttpParams = new HttpParams()
  ): Observable<any> {
    return this.apiService.uploadFile(path, data, params);
  }

  getFile(path?: string, params: HttpParams = new HttpParams()): Observable<Blob> {
    return this.apiService.getFile(path, params);
  }

  getPlainText(path?: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.apiService.getPlainText(path, params);
  }

}
