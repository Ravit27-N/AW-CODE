import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CxmDirectoryService {



  constructor(private apiService: ApiService) {
  }

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

  file(path: string, params: HttpParams): Observable<any> {
    return this.apiService.getFile(path, params);
  }

  uploadFile(path: string, formData: FormData) {
    return this.apiService.uploadFileWithProgress<any>(path, formData);
  }

  uploadFileWithParams(path: string, formData: FormData, params: HttpParams = new HttpParams()) {
    return this.apiService.uploadFile(path, formData, params);
  }

  getFileWithFileName(path?: string, params: HttpParams = new HttpParams()) {
    return this.apiService.getFileWithFileName(path, params);
  }
}
