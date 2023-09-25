import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root',
})
export class FileManagerService {
  constructor(private api: ApiService) {}

  getMaxUploadFileSize(): Observable<number> {
    return this.api.get(
      `${environment.rmsContextPath}/file-manager/max-file-size`,
    );
  }
}
