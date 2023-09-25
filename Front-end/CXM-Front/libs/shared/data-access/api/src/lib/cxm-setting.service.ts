import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CxmSettingService {
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

  put(path: string, data?: any): Observable<any> {
    return this.apiService.put(path, data);
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

  uploadFile(path: string, form: FormData, params: HttpParams = new HttpParams()): Observable<any> {
    return this.apiService.uploadFileWithProgress(path, form, params);
  }

  base64File(path: string,params: HttpParams = new HttpParams()): Observable<any> {
    return this.apiService.getBase64File(path, params);
  }
}
