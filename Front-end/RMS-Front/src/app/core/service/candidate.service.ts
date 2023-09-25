import {
  CandidateCriteria,
  CandidateDetail,
  CandidateFormModel,
  CandidateList,
  CandidateListReport,
  CandidateModel, FileInfoModel,
} from '../model';
import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { BlobResource } from '../../shared';

@Injectable()
export class CandidateService {
  constructor(private api: ApiService) {}

  create(formData: any): Observable<any> {
    return this.api.post(`${environment.rmsContextPath}/candidate`, formData);
  }

  update(formData: any): Observable<any> {
    return this.api.put(`${environment.rmsContextPath}/candidate`, formData);
  }

  delete(id?: number | string, isDelete?: boolean): Observable<any> {
    return this.api.patch(
      `${environment.rmsContextPath}/candidate/${id}/delete/${isDelete}`,
    );
  }

  hardDelete(id?: number): Observable<any> {
    return this.api.delete(`${environment.rmsContextPath}/candidate/${id}`);
  }

  restore(id?: number | string, isDelete?: boolean): Observable<any> {
    return this.api.patch(
      `${environment.rmsContextPath}/candidate/${id}/delete/${isDelete}`,
    );
  }

  updateStatusCandidate(
    id: number | string,
    statusId: number | string,
  ): Observable<any> {
    return this.api.patch(
      `${environment.rmsContextPath}/candidate/${id}/status/${statusId}`,
    );
  }

  getCandidates(criteria: CandidateCriteria): Observable<CandidateList> {
    let params = new HttpParams();
    if (criteria.defaultCriteria.pageIndex !== undefined) {
      const pageIndex = criteria.defaultCriteria.pageIndex;
      params = params.set('page', pageIndex.toString());
    }
    if (criteria.defaultCriteria.pageSize !== undefined) {
      params = params.set(
        'pageSize',
        criteria.defaultCriteria.pageSize.toString(),
      );
    }
    if (criteria.filter !== undefined) {
      params = params.set('filter', criteria?.filter?.toString());
    }
    if (criteria.defaultCriteria.sortDirection !== undefined) {
      params = params.set(
        'sortDirection',
        criteria.defaultCriteria.sortDirection.toString(),
      );
    }
    if (criteria.defaultCriteria.sortByField !== undefined) {
      params = params.set(
        'sortByField',
        criteria.defaultCriteria.sortByField.toString(),
      );
    }
    if (criteria.filterReminderOrInterview !== undefined) {
      params = params.set(
        'filterReminderOrInterview',
        criteria.filterReminderOrInterview.toString(),
      );
    }
    if (criteria.status !== undefined) {
      params = params.set('status', criteria?.status?.toString());
    }
    if (criteria.isDeleted !== undefined) {
      params = params.set('isDeleted', criteria.isDeleted.toString());
    }
    return this.api.get(`${environment.rmsContextPath}/candidate`, params);
  }

  getList(
    page?: number,
    pageSize?: number,
    filter?: string | number | Date,
    sortDirection?: string,
    sortByField?: string,
    filterReminderOrInterview?: string,
    status?: string,
    isDeleted?: boolean,
  ): Observable<CandidateList> {
    let params = new HttpParams();
    if (page !== undefined) {
      params = params.set('page', page.toString());
    }
    if (pageSize !== undefined) {
      params = params.set('pageSize', pageSize.toString());
    }
    if (filter !== undefined) {
      params = params.set('filter', filter.toString());
    }
    if (sortDirection !== undefined) {
      params = params.set('sortDirection', sortDirection.toString());
    }
    if (sortByField !== undefined) {
      params = params.set('sortByField', sortByField.toString());
    }
    if (filterReminderOrInterview !== undefined) {
      params = params.set(
        'filterReminderOrInterview',
        filterReminderOrInterview.toString(),
      );
    }
    if (status !== undefined) {
      params = params.set('status', status.toString());
    }
    if (isDeleted !== undefined) {
      params = params.set('isDeleted', isDeleted.toString());
    }
    return this.api.get(`${environment.rmsContextPath}/candidate`, params);
  }

  getAvailableResource(httpParams: HttpParams, demandId: number) {
    return this.api.get(`${environment.rmsContextPath}/candidate/${demandId}/available`, httpParams).toPromise();
  }

  getCandidateDetailById(id: number | string): Observable<CandidateModel> {
    return this.api.get(`${environment.rmsContextPath}/candidate/${id}/view`);
  }

  getCandidateDetail(id: number): Observable<CandidateDetail> {
    return this.api.get(`${environment.rmsContextPath}/candidate/${id}/view`);
  }

  getCandidateDetails(id: number): Observable<CandidateModel> {
    return this.api.get(`${environment.rmsContextPath}/candidate/${id}/view`);
  }

  getById(id: number | string): Observable<CandidateFormModel> {
    return this.api.get(`${environment.rmsContextPath}/candidate/${id}`);
  }

  getProfileURL(candidateId: number, fileId: string) {
    return `${environment.apiUrl}${environment.rmsContextPath}/candidate/${candidateId}/view/${fileId}`;
  }

  updateStatusDetailCandidate(
    id: number,
    formData: CandidateFormModel,
  ): Observable<any> {
    return this.api.patch(
      `${environment.rmsContextPath}/candidate/${id}/status`,
      {
        statusId: formData.statusId,
      },
    );
  }

  getCandidateListByFilter(
    name?: string,
    gender?: string,
    school?: string,
    gpa?: number,
    position?: string,
    index?: number,
    size?: number,
  ): Observable<CandidateList> {
    return this.api.get(
      `${environment.rmsContextPath}/candidate/${name}/${gender}/${school}/${gpa}/${position}/${index}/${size}`,
    );
  }

  getCandidateReport(
    school?: Date | number,
    to?: Date | number,
    index?: number,
    size?: number,
  ): Observable<CandidateList> {
    return this.api.get(
      `${environment.rmsContextPath}/candidate/report/${school}/${to}/${index}/${size}`,
    );
  }

  getCandidateReports(
    from: Date | string,
    to: Date | string,
    page?: number,
    pageSize?: number,
    sortDirection?: string,
    sortByField?: string,
    filter?: string,
  ): Observable<CandidateListReport> {
    let params = new HttpParams();
    if (from !== undefined) {
      params = params.set('from', from.toString());
    }
    if (to !== undefined) {
      params = params.set('to', to.toString());
    }
    if (page !== undefined) {
      params = params.set('page', page.toString());
    }
    if (pageSize !== undefined) {
      params = params.set('pageSize', pageSize.toString());
    }
    if (sortDirection !== undefined) {
      params = params.set('sortDirection', sortDirection.toString());
    }
    if (sortByField !== undefined) {
      params = params.set('sortByField', sortByField.toString());
    }
    if (filter !== undefined) {
      params = params.set('filter', filter.toString());
    }
    return this.api.get(
      `${environment.rmsContextPath}/candidate/report`,
      params,
    );
  }

  // eslint-disable-next-line max-len
  getAdvanceReports(
    from?: Date | string,
    to?: Date | string,
    position?: string,
    page?: number,
    pageSize?: number,
    sortDirection?: string,
    sortByField?: string,
  ): Observable<any> {
    let params = new HttpParams();
    if (from !== undefined) {
      params = params.set('from', from.toString());
    }
    if (to !== undefined) {
      params = params.set('to', to.toString());
    }
    if (position !== undefined) {
      params = params.set('position', position.toString());
    }
    if (page !== undefined) {
      params = params.set('page', page.toString());
    }
    if (pageSize !== undefined) {
      params = params.set('pageSize', pageSize.toString());
    }
    if (sortDirection !== undefined) {
      params = params.set('sortDirection', sortDirection.toString());
    }
    if (sortByField !== undefined) {
      params = params.set('sortByField', sortByField.toString());
    }
    return this.api.get(
      `${environment.rmsContextPath}/candidate/advance-report`,
      params,
    );
  }

  advanceSearch(
    criteria?: any,
    size?: number,
    index?: number,
  ): Observable<any> {
    let params = new HttpParams();
    if (size) {
      params = params.set('pageSize', size.toString());
    }
    if (index) {
      params = params.set('page', index.toString());
    }

    Object.keys(criteria).forEach((k) => {
      if (criteria[k] && criteria[k] !== '') {
        params = params.set(k, criteria[k]);
      }
    });

    return this.api.get(
      `${environment.rmsContextPath}/candidate/advancedSearch`,
      params,
    );
  }

  emailValidation(email: string): Observable<number> {
    return this.api.get(
      `${environment.rmsContextPath}/candidate/validateEmail/${email}`,
    );
  }

  uploadCandidateProfile(fileName: any): Observable<any> {
    return this.api.upload(
      `${environment.rmsContextPath}/candidate/profile/upload`,
      fileName,
    );
  }

  updateCandidateProfile(id: number | string, filename: any): Observable<any> {
    return this.api.updatePhoto(
      `${environment.rmsContextPath}/candidate/${id}/profile/onUpdate`,
      filename,
    );
  }

  getCandidateProfile(id: number | string, photoUrl: string): Observable<any> {
    return this.api.getPhoto(
      `${environment.rmsContextPath}/candidate/${id}/view/${photoUrl}`,
    );
  }

  uploadCandidateFiles(files: FormData): Observable<any> {
    return this.api.upload(
      `${environment.rmsContextPath}/candidate/attach/upload`,
      files,
    );
  }

  getCandidateFileNames(candidateId: number | string): Observable<any> {
    return this.api.getFileNames(
      `${environment.rmsContextPath}/candidate/${candidateId}/attach`,
    );
  }

  // previewFile(filename: string): Observable<any>{
  //   return ;
  // }

  // removeFile(filename: string): Observable<any>{
  //   return null;
  // }

  previewCandidateFile(
    candidateId: number | string,
    filename: string,
  ): Observable<any> {
    return this.api.getFiles(
      `${environment.rmsContextPath}/candidate/${candidateId}/download/${filename}`,
    );
  }

  removeCandidateFile(
    candidateId: number | string,
    filename: string,
  ): Observable<any> {
    return this.api.delete(
      `${environment.rmsContextPath}/candidate/${candidateId}/remove/${filename}`,
    );
  }

  updateCandidateFile(
    candidateId: number | string,
    files: FormData,
  ): Observable<any> {
    return this.api.updateFiles(
      `${environment.rmsContextPath}/candidate/${candidateId}/attach/onUpload`,
      files,
    );
  }

  getExportAdvanceReports(
    from?: Date | string,
    to?: Date | string,
    position?: string,
    page?: number,
    pageSize?: number,
    sortDirection?: string,
    sortByField?: string,
  ): Observable<BlobResource> {
    let params = new HttpParams();
    if (from !== undefined) {
      params = params.set('from', from.toString());
    }
    if (to !== undefined) {
      params = params.set('to', to.toString());
    }
    if (position !== undefined) {
      params = params.set('position', position.toString());
    }
    if (page !== undefined) {
      params = params.set('page', page.toString());
    }
    if (pageSize !== undefined) {
      params = params.set('pageSize', pageSize.toString());
    }
    if (sortDirection !== undefined) {
      params = params.set('sortDirection', sortDirection.toString());
    }
    if (sortByField !== undefined) {
      params = params.set('sortByField', sortByField.toString());
    }
    return this.api.getFileWithParam(
      `${environment.rmsContextPath}/candidate/export-advance-report`,
      params,
    );
  }

  uploadCandidateCV(formData: FormData): Observable<any> {
    return of({
      fileSize: 30000,
      fileName: 'my-cv.pdf',
    });
  }

  async encodeBlobToUTF8(blob: Blob): Promise<string> {
    const arrayBuffer = await blob.arrayBuffer();
    return new TextDecoder('utf-8').decode(arrayBuffer);
  }

  checkDuplicatedEmail(email: string) {
    return this.api.get(
      `${environment.rmsContextPath}/candidate/validateEmail/${email}`,
    );
  }

  getUniversityDegreeTypes(): Observable<string[]> {
    return this.api.get(`${environment.rmsContextPath}/candidate/degree-type`);
  }

  getUniversityExperienceLevels(): Observable<string[]> {
    return this.api.get(
      `${environment.rmsContextPath}/candidate/experience-level`,
    );
  }

  getFileBase64(candidateId: number, fileId: string): Observable<FileInfoModel> {
    return this.api.get(`${environment.rmsContextPath}/candidate/${candidateId}/file/${fileId}`);
  }

  getFileURL(candidateId: number, fileId: string): string {
    return `${environment.apiUrl}/candidate/${candidateId}/view/${fileId}`;
  }
}
