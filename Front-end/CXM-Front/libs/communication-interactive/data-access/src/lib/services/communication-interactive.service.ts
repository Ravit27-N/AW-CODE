import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CxmTemplateService } from '@cxm-smartflow/shared/data-access/api';
import { templateEnv } from '@env-cxm-template';
import { Observable } from 'rxjs';
import { CommunicationInteractiveResponse } from '../models';



@Injectable({
  providedIn: 'root'
})
export class CommunicationInteractiveService {

  constructor(private readonly templateService: CxmTemplateService) {
  }

  public getAll(filters?: { filter: string }): Observable<CommunicationInteractiveResponse> {

    let params = new HttpParams();
    params = params.append('page', 1);
    // params = params.append('pageSize', 0);
    params = params.append('sortDirection', 'asc');

    if(filters && filters.filter) {
      params = params.append('filter', filters.filter );
    }

    return this.templateService.get(`${templateEnv.templateContext}/communication-interactive/template`, params);
  }

  public getRemotedUrl(id: number): Observable<string> {
    return this.templateService.getPlainText(`${templateEnv.templateContext}/communication-interactive/template/${id}/remote-url`);
  }
}
