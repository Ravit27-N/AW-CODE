import { HttpClient, HttpEvent, HttpHeaders, HttpParams, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import { ConfigurationService } from './configuration.service';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  baseUrl: string;

  constructor(
    private http: HttpClient,
    configService: ConfigurationService
    ) {
      const settings = configService.getAppSettings();
      Object.assign(this, { baseUrl: settings.apiGateway })
    }

  get headers(): HttpHeaders {
    const headersConfig = {
      'Content-Type': 'application/json',
      Accept: 'application/json',
    };

    return new HttpHeaders(headersConfig);
  }

  get<T>(path?: string, params: HttpParams = new HttpParams()): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${path}`, {
      headers: this.headers,
      params,
    }).pipe(catchError(e =>  throwError(e)));
  }

  post<T, D>(path: string, data: D, params: HttpParams = new HttpParams()): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${path}`, JSON.stringify(data), { headers: this.headers, params })
    .pipe(catchError(e => throwError(e)));
  }

  put<T, D>(path: string, data: D, params: HttpParams = new HttpParams()): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${path}`, JSON.stringify(data), {
      headers: this.headers,
      params
    })
    .pipe(catchError(e => throwError(e)));
  }

  delete<T>(path?: string): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}${path}`, {
      headers: this.headers,
    })
    .pipe(catchError(e => throwError(e)));
  }

  deleteWithParams<T>(path: string, params: HttpParams = new HttpParams()): Observable<T>{
    return this.http.delete<T>(`${this.baseUrl}${path}`, {
      headers: this.headers,
      params
    })
      .pipe(catchError(e => throwError(e)));
  }

  patch<T>(path: string, data?: any): Observable<T>{
    return this.http.patch<T>(`${this.baseUrl}${path}`, JSON.stringify(data),{
      headers: this.headers,
    })
      .pipe(catchError(e => throwError(e)));
  }

  getImageFile(path: string): Observable<any>{
    return this.http.get(`${path}`, {
      headers: this.headers,
      responseType: 'blob'
    }).pipe(catchError(e =>  throwError(e)));
  }

  uploadFile(path: string, data: FormData,   params: HttpParams = new HttpParams()): Observable<any> {
    return this.http.post(`${this.baseUrl}${path}`, data, {
      reportProgress: true,
      observe: 'events',
      params
    });
  }

  getFile(path?: string, params: HttpParams = new HttpParams()): Observable<Blob> {

    this.http.get(`${this.baseUrl}${path}`, {
      headers: this.headers,
      params,
      responseType: 'blob',
      observe: 'response',
    }).subscribe((response: any) => {
      const header_content = response?.headers?.get('Content-Disposition');
      const filename = header_content?.split('filename=')[1]?.split('.')[0]?.replace(/_/g, '-');
    })
    return this.http.get(`${this.baseUrl}${path}`, {
      headers: this.headers,
      params,
      responseType: 'blob',
    });
  }

  getFileWithFileName(path?: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.http.get(`${this.baseUrl}${path}`, {
      headers: this.headers,
      params,
      responseType: 'blob',
      observe: 'response',
    }).pipe(map((value) => {
      const header_content = value?.headers?.get('Content-Disposition');
      const filename = header_content?.split('filename=')[1]?.split('.')[0];

      return {
        file: value?.body,
        filename: filename
      };
    }));
  }

  getFileWithHeader(path?: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.http.get(`${this.baseUrl}${path}`, {
      headers: this.headers,
      params,
      responseType: 'blob',
      observe: 'response',
    }).pipe(map((value) => {
      const header_content = value?.headers?.get('Content-Disposition');

      return {
        file: value?.body,
        header_content,
      };
    }));
  }


  getBase64File(path: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.http.get(`${this.baseUrl}${path}`, {
      headers: new HttpHeaders({'Content-Type': 'text/plain', Accept: "application/octet-stream"}),
      params,
      responseType: 'text'
    });
  }

  downloadBase64(path: string, params: HttpParams = new HttpParams()): Observable<HttpEvent<any>> {
    return this.http.get(`${this.baseUrl}${path}`, {
      headers: new HttpHeaders({'Content-Type': 'text/plain', Accept: "application/octet-stream"}),
      params,
      responseType: 'text',
      reportProgress: true,
      observe: 'events'
    });
  }

  /**
   * Method used to get value of type (text/plain;charset=ISO-8859-1).
   * @param url
   * @param path
   * @param params
   * @returns value of {@link Observable}
   */
  getPlainText(path?: string, params: HttpParams = new HttpParams()): Observable<any>{
    return this.http.get(`${this.baseUrl}${path}`, {
      headers: this.headers,
      params,
      responseType: 'text'
    }
    ).pipe(catchError(e => throwError(e)));

  }

  uploadFileWithProgress<T>(path: string, form: FormData, params: HttpParams = new HttpParams()): Observable<HttpEvent<T>> {
    const request = new HttpRequest('POST', `${this.baseUrl}${path}`, form, {
      reportProgress: true,
      responseType: 'json',
      params: params
    })
    return this.http.request<T>(request).pipe(catchError(e => throwError(e)))
  }

}
