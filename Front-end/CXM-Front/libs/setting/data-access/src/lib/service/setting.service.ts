import { Injectable } from '@angular/core';
import { CxmSettingService } from '@cxm-smartflow/shared/data-access/api';
import { Observable, of } from 'rxjs';
import { settingEnv } from '@env-cxm-setting';
import { Params } from '@cxm-smartflow/shared/data-access/model';
import { HttpErrorResponse, HttpParams, HttpStatusCode } from '@angular/common/http';
import { ResourceTypeConstantEn, ResourceTypeConstantFr } from '@cxm-smartflow/setting/util';
import { CreateResourcePayload } from '../model';

@Injectable({ providedIn: 'root' })
export class SettingService {
  constructor(private readonly cxmSettingService: CxmSettingService) {}

  getCriteria(): Observable<any> {
    const locale = localStorage.getItem('locale') || 'fr';
    if (locale == 'fr') {
      return of(ResourceTypeConstantFr);
    }

    return of(ResourceTypeConstantEn);
  }

  getAll(params: Params, types: any): Observable<any> {
    let httpParams = new HttpParams();
    const locale = localStorage.getItem('locale') || 'fr';
      Object.entries(params)
        .forEach(([key, value]) => {
          httpParams = httpParams.set(key, value);
      });

      httpParams = httpParams.set('types', types);
      httpParams = httpParams.set("language",locale);
    return this.cxmSettingService.get(`${settingEnv.settingContext}/resources`, httpParams);
  }
  create(params: CreateResourcePayload): Observable<any> {
    return this.cxmSettingService.post(`${settingEnv.settingContext}/resources`, params);
  }

  /**
   * Method used to upload resource file.
   * @param formData - object of {@link FormData}.
   * @param type - type of resource {@link string}.
   */
  uploadFile(formData: FormData, type: string): Observable<any> {
    if (type === undefined || type === null) {
      throw new HttpErrorResponse({ status: HttpStatusCode.BadRequest, statusText: 'Type must be not null' });
    }

    let httpParams = new HttpParams();
    httpParams = httpParams.set('type', type);

    return this.cxmSettingService.uploadFile(`${settingEnv.settingContext}/storage/store`, formData, httpParams);
  }

  checkDuplicateLabel(label: string, type: string): Observable<boolean> {
    if (type === undefined || type === null) {
      throw new HttpErrorResponse({ status: HttpStatusCode.BadRequest, statusText: 'Type must be not null' });
    }

    let httpParams = new HttpParams();
    httpParams = httpParams.set('type', type);

    return this.cxmSettingService.get(`${settingEnv.settingContext}/resources/duplicate/${label}`, httpParams);
  }

  delete(fileId: string): Observable<any> {
    return this.cxmSettingService.delete(`${settingEnv.settingContext}/resources/${fileId}`);
  }

  deleteTemp(fileId: string): Observable<any> {
    return this.cxmSettingService.delete(`${settingEnv.settingContext}/storage/${fileId}`);
  }

  downloadFile(fileId: string): Observable<any> {
    return this.cxmSettingService.base64File(`${settingEnv.settingContext}/resources/file/${fileId}`);
  }

  fetchTechnicalName(fileId: string): Observable<any> {
    return this.cxmSettingService.get(`${settingEnv.settingContext}/resources/technicalName/${fileId}`);
  }
}
