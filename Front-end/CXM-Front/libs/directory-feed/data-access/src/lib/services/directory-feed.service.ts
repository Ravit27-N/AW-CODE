import {Injectable} from '@angular/core';
import {CxmDirectoryService, CxmUserService,} from '@cxm-smartflow/shared/data-access/api';
import {Params} from '@cxm-smartflow/shared/data-access/model';
import {Observable} from 'rxjs';
import {HttpParams} from '@angular/common/http';
import {directoryEnv as env} from '@env-cxm-directory';
import {
  CheckExistValueParamModel,
  DirectoryFeedField,
  DirectoryFeedForm,
  DirectoryFeedListResponse,
  ImportCsvRequestModel,
  ListDirectoryFeedValue
} from '../models';
import {cxmProfileEnv} from '@env-cxm-profile';

@Injectable({
  providedIn: 'root',
})
export class DirectoryFeedService {
  constructor(
    private cxmDefinitionService: CxmDirectoryService,
    private cxmUserService: CxmUserService
  ) {}

  getDirectoryFeedList(params: Params): Observable<DirectoryFeedListResponse> {
    let params$ = new HttpParams();
    if (params !== undefined) {
      Object.entries(params).forEach(([key, value]) => {
        params$ = params$.set(key, value);
      });
    }

    return this.cxmDefinitionService.get(
      `${env.directoryContext}/directory-definition/shared`,
      params$
    );
  }

  getUserDetail() {
    return this.cxmUserService.get(
      `${cxmProfileEnv.profileContext}/users/check-user-is-admin`
    );
  }

  getDefinitionDirectoryList(
    http: HttpParams
  ): Observable<DirectoryFeedListResponse> {
    return this.cxmDefinitionService.get(
      `${env.directoryContext}/directory-definition/shared`,
      http
    );
  }

  /**
   * ============= Directory Feed =============
   */

  getDirectoryFeedField(directoryId: number): Observable<DirectoryFeedField> {
    return this.cxmDefinitionService.get(
      `${env.directoryContext}/directory-feed/${encodeURIComponent(
        directoryId
      )}/fields`
    );
  }

  getDirectoryFeedValue(
    directoryId: number,
    options?: {
      page?: number;
      pageSize?: number;
      filter?: string;
      sortByField?: string;
      sortDirection?: string;
    }
  ): Observable<ListDirectoryFeedValue> {
    let params = new HttpParams();

    if (options && options.page && options.pageSize) {
      params = params.append('page', options.page);
      params = params.append('pageSize', options.pageSize);
      params = params.append('filter', options?.filter || '');
      params = params.append('sortByField', options?.sortByField || '');
      params = params.append('sortDirection', options?.sortDirection || '');
    }

    return this.cxmDefinitionService.get(
      `${env.directoryContext}/directory-feed/${directoryId}/values`,
      params
    );
  }

  getDirectoryDataFile(directoryId: string) {
    return this.cxmDefinitionService.file(
      `${env.directoryContext}/directory-feed/${encodeURIComponent(
        directoryId
      )}/export-csv`,
      new HttpParams()
    );
  }

  uploadCsvFile(formData: FormData, httpParams: HttpParams) {
    return this.cxmDefinitionService.uploadFileWithParams(
      `${env.directoryContext}/directory-feed/upload-csv`,
      formData,
      httpParams
    );
  }

  removeCsvFile(fileId: string) {
    return this.cxmDefinitionService.delete(
      `${env.directoryContext}/directory-feed/${fileId}`
    );
  }

  downloadDirectoryFeedCsv(id: number) {
    return this.cxmDefinitionService.getFileWithFileName(
      `${env.directoryContext}/directory-feed/${id}/export-csv`
    );
  }

  exportFeedCsv(httpParams: HttpParams) {
    return this.cxmDefinitionService.get(
      `${env.directoryContext}/directory-feed/export`,
      httpParams
    );
  }

  importCsvDirectoryValue(directoryId: number, data: ImportCsvRequestModel) {
    return this.cxmDefinitionService.post(
      `${env.directoryContext}/directory-feed/${directoryId}/import`,
      data
    );
  }

  submitDirectoryFeedValue(directoryId: number, data: DirectoryFeedForm) {
    return this.cxmDefinitionService.post(
      `${env.directoryContext}/directory-feed/${directoryId}/submit`,
      data
    );
  }

  checkExistValue(directoryId: number, checkExistValueParamModel: CheckExistValueParamModel): Observable<boolean> {
    const {fieldId, id, value} = checkExistValueParamModel;
    let params = new HttpParams();
    params = params.append('value', value);
    return this.cxmDefinitionService.get(
      `${env.directoryContext}/directory-feed/${directoryId}/value/${fieldId}/is-exist/${id}`, params
    );
  }
  
}
