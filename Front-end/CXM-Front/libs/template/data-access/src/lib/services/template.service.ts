import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { templateEnv as envTemplate } from '@env-cxm-template';
import { CxmTemplateService } from '@cxm-smartflow/shared/data-access/api';
import {
  TemplateList,
  TemplateModel,
} from '@cxm-smartflow/shared/data-access/model';
import {
  GrapeJsAsset,
  UploadImageAsset,
} from '../model';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TemplateService {
  constructor(private cxmTemplateService: CxmTemplateService) {}

  createEmailTemplate(formData: any): Observable<any> {
    let params$ = new HttpParams();
    if (formData?.width != undefined) {
      params$ = params$.set('width', formData.width);
    }
    if (formData?.height !== undefined) {
      params$ = params$.set('height', formData.height);
    }

    return this.cxmTemplateService.post(
      `${envTemplate.templateContext}/templates`,
      formData,
      params$
    );
  }

  updateEmailTemplate(formData: any): Observable<any> {
    let params$ = new HttpParams();
    if (formData?.width != undefined) {
      params$ = params$.set('width', formData.width);
    }
    if (formData?.height !== undefined) {
      params$ = params$.set('height', formData.height);
    }

    return this.cxmTemplateService.put(
      `${envTemplate.templateContext}/templates`,
      formData,
      params$
    );
  }

  validationModelName(value: string, templateType: string, templateId?: number): Observable<any> {
    let params$ = new HttpParams();
    if (templateId && templateId > 0) {
      params$ = params$.set('id', templateId);
    }

    if (value?.length <= 0) {
      return of(false);
    } else {
      return this.cxmTemplateService.get(
        `${envTemplate.templateContext}/templates/check/${value}/type/${templateType}`,
        params$
      );
    }
  }

  getAllTemplate(
    page?: number,
    pageSize?: number,
    sortByField = 'createdAt',
    sortDirection = 'asc',
    filter = '',
    templateType = ''
  ): Observable<TemplateList> {
    let params$ = new HttpParams();

    params$ = params$
      .set('sortByField', sortByField)
      .set('sortDirection', sortDirection)
      .set('filter', filter)
      .set('templateType', templateType);
    return this.cxmTemplateService.get(
      `${envTemplate.templateContext}/templates/${page}/${pageSize}`,
      params$
    );
  }

  listChooseModelEmailTemplate(
    page?: number,
    pageSize?: number,
    sortByField?: string,
    sortDirection?: string,
    filter?: string,
    templateType?: string
  ): Observable<TemplateList> {
    let params$ = new HttpParams();

    params$ = params$.set('sortByField', sortByField || '');
    params$ = params$.set('sortDirection', sortDirection || '');
    params$ = params$.set('filter', filter || '');
    params$ = params$.set('templateType', templateType || '');

    return this.cxmTemplateService.get(
      `${envTemplate.templateContext}/templates/choose-model/${page}/${pageSize}`,
      params$
    );
  }

  deleteEmailTemplate(id: number): Observable<any> {
    return this.cxmTemplateService.delete(
      `${envTemplate.templateContext}/templates/${id}`
    );
  }

  getTemplateById(id: number): Observable<TemplateModel> {
    return this.cxmTemplateService.getById(
      `${envTemplate.templateContext}/templates/${id}`
    );
  }

  /**
   * Method used to upload assets file.
   * @param file
   * @returns
   */
  uploadAssetsOfGrapeJs(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file, file.name);
    return this.cxmTemplateService.upload(
      `${envTemplate.templateContext}/templates/composition/assets/store`,
      formData
    );
  }

  deleteAssetOfGrapeJs(assetId: number): Observable<any>{
    return this.cxmTemplateService.delete(`${envTemplate.templateContext}/templates/composition/assets/${assetId}`);
  }

  loadAllTemplateCompositions(): Observable<GrapeJsAsset[]> {
    return this.cxmTemplateService.get(
      `${envTemplate.templateContext}/templates/composition/assets`
    );
  }

  saveAssetUrl(uploadAsset: UploadImageAsset): Observable<GrapeJsAsset> {
    return this.cxmTemplateService.post(
      `${envTemplate.templateContext}/templates/composition/assets/url`,
      uploadAsset
    );
  }

  downloadTemplate(template: TemplateModel) {
    if (template && template.id)
      return this.cxmTemplateService.getFile(
        `${envTemplate.templateContext}/storage/download/${encodeURIComponent(
          template.id
        )}`
      );

    return of();
  }

  getDefaultVariable() {
    return this.cxmTemplateService.get(`${envTemplate.templateContext}/templates/email/default-variable`);
  }
}
