import { HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
// import { flowDepositEnv } from '@env-flow-deposit';
import { Observable } from "rxjs";
import { ApiService } from "..";


@Injectable({
  providedIn: 'root'
})
export class CxmFlowDepositService {



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

  uploadFile(path: string, form: FormData, params: HttpParams = new HttpParams()) {
    return this.apiService.uploadFileWithProgress<any>(path, form, params)
  }

  base64File(path: string): Observable<any> {
    return this.apiService.getBase64File(path);
  }

  getLimitUploadSize(path: string): Observable<any> {
    return this.apiService.getPlainText(path);
  }

  singleUpload(path: string, form: FormData, params: HttpParams = new HttpParams()) {
    return this.apiService.uploadFile(path, form, params)
  }

  constructor(private apiService: ApiService) { }
}
