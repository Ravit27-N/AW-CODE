import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import {catchError, map} from 'rxjs/operators';

@Injectable()
export class ApiService {

  header: HttpHeaders = new HttpHeaders();

  get headers(): HttpHeaders {
    const headersConfig = {
      'Content-Type': 'application/json',
      Accept: 'application/json',
    };

    return new HttpHeaders(headersConfig);
  }
  constructor(private http: HttpClient) {
    this.header = this.header.set('Content-Type', 'application/json');
  }

  get(path: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.http.get(`${environment.apiUrl}${path}`, { params })
      .pipe(catchError(this.formatErrors));
  }

  post(path: string, body: any = {}): Observable<any> {
    return this.http.post(
      `${environment.apiUrl}${path}`,
      JSON.stringify(body), { headers: this.header }
    ).pipe(catchError(this.formatErrors));
  }

  put(path: string, body: any = {}): Observable<any> {
    return this.http.put(
      `${environment.apiUrl}${path}`,
      JSON.stringify(body),
      { headers: this.header }
    ).pipe(catchError(this.formatErrors));
  }

  delete(path): Observable<any> {
    return this.http.delete(
      `${environment.apiUrl}${path}`
    ).pipe(catchError(this.formatErrors));
  }

  patch(path: string, body: any = {}): Observable<any> {
    return this.http.patch(`${environment.apiUrl}${path}`,
      JSON.stringify(body),
      { headers: this.header }
    );
  }

  viewFile(path: string): Observable<any> {
    return this.http.get(`${environment.apiUrl}${path}`, { responseType: 'blob' })
      .pipe(catchError(this.formatErrors));
  }

  upload(path: string, formdata: FormData, params: HttpParams = new HttpParams()): Observable<any> {
    return this.http.post(`${environment.apiUrl}${path}`,
      formdata, {
      params,
      reportProgress: true
    })
      .pipe(catchError(this.formatErrors));
  }

  private formatErrors(error: any): Observable<any> {
    return throwError(error.error);
  }

  updateFiles(path: string, formData: FormData, params: HttpParams = new HttpParams()): Observable<any> {
    return this.http.put(`${environment.apiUrl}${path}`,
      formData, {
      params,
      reportProgress: true
    })
      .pipe(catchError(this.formatErrors));
  }

  updatePhoto(path: string, formdata: FormData): Observable<any> {
    return this.http.put(`${environment.apiUrl}${path}`,
      formdata,
      {
        responseType: 'text'
      })
      .pipe(catchError(this.formatErrors));
  }

  getPhoto(path: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.http.get(`${environment.apiUrl}${path}`,
      { params, headers: { 'content-Type': 'image/jpg/png/jpeg' }, responseType: 'arraybuffer' })
      .pipe(catchError(this.formatErrors));
  }

  getFiles(path: string): Observable<any>{
    return this.http.get(`${environment.apiUrl}${path}`, {responseType: 'blob'})
    .pipe(catchError(this.formatErrors));
  }

  getFileNames(path: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.http.get(`${environment.apiUrl}${path}`, { params })
      .pipe(catchError(this.formatErrors));
  }
  getFileWithParam(path?: string, params: HttpParams = new HttpParams()) {
    return this.getFileWithFileName(path, params);
  }
  getFileWithFileName(path?: string, params: HttpParams = new HttpParams()): Observable<any> {
    return this.http.get(`${environment.apiUrl}${path}`, {
      headers: this.headers,
      params,
      responseType: 'blob',
      observe: 'response',
    }).pipe(map((value) => {

      const headerContent = value?.headers?.get('Content-Disposition');
      const filename = headerContent?.split('filename=')[1]?.split('.')[0];

      return {
        file: value?.body,
        filename
      };
    }));
  }
}
