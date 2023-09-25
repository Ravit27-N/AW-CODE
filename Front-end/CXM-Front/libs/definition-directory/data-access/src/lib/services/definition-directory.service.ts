import { Injectable } from '@angular/core';
import { CxmDirectoryService, CxmUserService } from '@cxm-smartflow/shared/data-access/api';
import { Params } from '@cxm-smartflow/shared/data-access/model';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { directoryEnv as env } from '@env-cxm-directory';
import {
  DefinitionDirectoryListModel,
  DirectoryDefinitionForm,
  FieldData,
  KeyValue,
  MaskAndTypeValidationResponse,
  UserDetailModel
} from '../models';
import { cxmProfileEnv } from '@env-cxm-profile';
import {DefinitionDirectoryListType} from "../models/definition-directory-list.type";

@Injectable({
  providedIn: 'root',
})
export class DefinitionDirectoryService {
  constructor(private cxmDefinitionService: CxmDirectoryService,
              private cxmUserService: CxmUserService) {}

  getDefinitionDirectoryList(params: Params): Observable<DefinitionDirectoryListModel> {
    let params$ = new HttpParams();
    if (params !== undefined) {
      Object.entries(params).forEach(([key, value]) => {
        params$ = params$.set(key, value);
      });
    }

    return this.cxmDefinitionService.get(
      `${env.directoryContext}/directory-definition`,
      params$
    );
  }

  getIsUseDefinitionDirectory(id: number): Observable<boolean> {
    return this.cxmDefinitionService.get(`${env.directoryContext}/directory-definition/${id}/is-used`);
  }

  deleteDefinitionDirectoryList(id: number): Observable<any> {
    return this.cxmDefinitionService.delete(`${env.directoryContext}/directory-definition/${id}`);
  }

  createDirectoryDefinition(
    directoryDefinitionForm: DirectoryDefinitionForm
  ): Observable<DirectoryDefinitionForm> {
    return this.cxmDefinitionService.post(
      `${env.directoryContext}/directory-definition`,
      directoryDefinitionForm
    );
  }

  editDirectoryDefinition(directoryDefinitionForm: DirectoryDefinitionForm): Observable<DirectoryDefinitionForm> {
    return this.cxmDefinitionService.put(
      `${env.directoryContext}/directory-definition`,
      directoryDefinitionForm
    );
  }

  getFieldData(language: string): Observable<FieldData[]> {
    return this.cxmDefinitionService.get(
      `${env.directoryContext}/directory-definition/field-data/key-value/${language}`
    );
  }

  getFieldTypes(): Observable<KeyValue[]> {
    return this.cxmDefinitionService.get(
      `${env.directoryContext}/directory-definition/field-type/key-value`
    );
  }

  getFieldOptions(): Observable<KeyValue[]> {
    return this.cxmDefinitionService.get(
      `${env.directoryContext}/directory-definition/field-option/key-value`
    );
  }

  getDataType(): Observable<KeyValue[]> {
    return this.cxmDefinitionService.get(
      `${env.directoryContext}/directory-definition/data-type/key-value`
    );
  }

  getDirectoryDefinitionById(id: number): Observable<DirectoryDefinitionForm>{
    return  this.cxmDefinitionService.get(`${env.directoryContext}/directory-definition/${id}`);
  }

  isUnique(directoryId: number, fieldId: number): Observable<boolean>{
    return this.cxmDefinitionService.get(`${env.directoryContext}/directory-feed/${directoryId}/field/${fieldId}/is-unique`);
  }

  getUserDetail(): Observable<UserDetailModel> {
    return this.cxmUserService.get(`${cxmProfileEnv.profileContext}/users/check-user-is-admin`);
  }

  getDefinitionDirectory(httpParams: HttpParams): Observable<DefinitionDirectoryListType> {
    return this.cxmDefinitionService.get(`${env.directoryContext}/directory-definition`, httpParams);
  }

  deleteDefinitionDirectory(id: number): Observable<any> {
    return this.cxmDefinitionService.delete(`${env.directoryContext}/directory-definition/${id}`);
  }

  checkDefinitionDirectoryNameUnique(directoryName: string): Observable<boolean> {
    const params = new HttpParams()
      .set('directoryName', directoryName);
    return this.cxmDefinitionService.get(`${env.directoryContext}/directory-definition/exists/name`, params
    );
  }

  downloadStructureFile(id: number): Observable<{ file: Blob, filename: string }> {
    return this.cxmDefinitionService.getFileWithFileName(`${env.directoryContext}/directory-definition/export-directory-structure/${id}`);
  }

  getDirectoryDefinitionDataType(): Observable<KeyValue[]> {
    return this.cxmDefinitionService.get(`${env.directoryContext}/directory-definition/data-type/key-value`
    );
  }

  getDirectoryDefinitionFieldData(language: string): Observable<FieldData[]> {
    return this.cxmDefinitionService.get(`${env.directoryContext}/directory-definition/field-data/key-value/${language}`);
  }

  getValidateType(directoryId: number, fieldId: number, type: string, mask: string): Observable<MaskAndTypeValidationResponse> {
    const maskBase64 = btoa(mask).replace(/=+$/, '');
    let params = new HttpParams();
    params = params.set('type', type);
    params = params.set('mask', maskBase64);

    return this.cxmDefinitionService.get(`${env.directoryContext}/directory-definition/validate-directory-type-mask/${directoryId}/${fieldId}`, params);
  }

}
