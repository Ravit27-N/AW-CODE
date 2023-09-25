import { ApiService } from './api.service';
import { HttpParams } from '@angular/common/http';
// import { campaignEnv as env } from '@env-cxm-campaign';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class CxmCampaignService {


  constructor(private apiService: ApiService) {}

  getList(path?: string, page?: number, pageSize?: number, sortByField?: string, sortDirection?: string, filter?: string, type?: string, mode?: string): Observable<any> {
    let params$ = new HttpParams();
    if (sortByField != undefined) {
      params$ = params$.set('sortByField', sortByField);
    }
    if (sortDirection !== undefined) {
      params$ = params$.set('sortDirection', sortDirection);
    }
    if (filter !== undefined) {
      params$ = params$.set('filter', filter);
    }
    if (type !== undefined) {
      params$ = params$.set('type', type);
    }

    if(mode !== undefined){
      params$ = params$.set('mode', mode);
    }

    return this.apiService.get(
      `${path}/${page}/${pageSize}`,
      params$
    );
  }

  get(path?: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.apiService.get(path, params);
  }


  /**
   * Method used to get value of {@link Observable} that convert from value of type (text/plain;charset=ISO-8859-1).
   * @param path
   * @param params
   * @returns value of {@link Observable}
   */
  getPlainText(path?: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.apiService.getPlainText(path, params)
  }

  /**
   * Method used to update column in storage.
   * @param path
   */
  patch<T>(path: string): Observable<T> {
    return this.apiService.patch(path);
  }

  post(path: string, data?: any): Observable<any> {
    return this.apiService.post(path, data);
  }

  put(path: string, data?: any): Observable<any> {
    return this.apiService.put(path, data);
  }

  delete(path?: string, data?: any): Observable<any> {
    return this.apiService.delete(path);
  }

  deleteWithParam(path: string, params: HttpParams = new HttpParams()): Observable<any>{
    return this.apiService.deleteWithParams(path, params);
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

  getLimitUploadSize(path: string): Observable<any> {
    return this.apiService.getPlainText(path);
  }
}
