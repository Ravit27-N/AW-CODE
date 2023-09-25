import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpParams } from '@angular/common/http';
import { FileInfoModel } from '../model';

@Injectable({
  providedIn: 'root',
})
export class TemporaryFileService {
  constructor(private apiService: ApiService) {}

  fileUpload(formData: FormData, type: string): Observable<FileInfoModel> {
    const params = new HttpParams()
      .set('isBase64', 'true')
      .set('type', type);
    return this.apiService.upload(
      `${environment.rmsContextPath}/temporary/upload`,
      formData,
      params,
    );
  }

  fileView(fileName: string): Observable<any> {
    return this.apiService.viewFile(
      `${environment.rmsContextPath}/temporary/view/${fileName}`,
    );
  }

  fileRemove(fileName: string): Observable<any> {
    return this.apiService.delete(
      `${environment.rmsContextPath}/temporary/${fileName}`,
    );
  }
}
