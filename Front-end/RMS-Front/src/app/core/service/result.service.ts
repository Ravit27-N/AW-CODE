import { Observable } from 'rxjs';
import { ResultFormModel, ResultList } from '../model';
import { ApiService } from './api.service';
import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import {environment} from '../../../environments/environment';

@Injectable()
export class ResultService {

  constructor(
    private apiService: ApiService
  ) { }

  getList(index?: number, size?: number): Observable<ResultList> {
    const params = new HttpParams();
    if (index) { params.set('page', index.toString()); }
    if (index) { params.set('size', size.toString()); }

    return this.apiService.get(`${environment.rmsContextPath}/result`, params);
  }

  update(formData: ResultFormModel): Observable<any> {
    return this.apiService.patch(`${environment.rmsContextPath}/result`, formData);
  }
}
