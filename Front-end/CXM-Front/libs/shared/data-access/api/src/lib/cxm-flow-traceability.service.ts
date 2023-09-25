// @ts-ignore
import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root',
})
export class CxmFlowTraceabilityService {

  constructor(private apiService: ApiService) {}

  get(path: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.apiService.get(path, params);
  }

  post(
    path: string,
    data?: any,
    params: HttpParams = new HttpParams()
  ): Observable<any> {
    return this.apiService.post(path, data, params);
  }

  put(path: string, data?: any, params: HttpParams = new HttpParams()): Observable<any> {
    return this.apiService.put(path, data, params);
  }

  patch(path: string, data?: any): Observable<any> {
    return this.apiService.patch(path, data);
  }

  delete(path: string, data?: any): Observable<any> {
    return this.apiService.delete(path);
  }

  file(path: string): Observable<any> {
    return this.apiService.getFile(path);
  }
  fileCsv(path: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.apiService.getFileWithFileName(path, params);
  }

  base64File(path: string,params: HttpParams = new HttpParams()): Observable<any> {
    return this.apiService.getBase64File(path, params);
  }

  getFlowDeposit(path: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.apiService.get(path, params);
  }
}
