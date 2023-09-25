
import { Observable } from 'rxjs';
import { CompanyProfileModel } from '../model';
import { ApiService } from './api.service';
import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';

@Injectable()
export class CompanyProfileService {

  constructor(private service: ApiService) {}
  update( formData: CompanyProfileModel): Observable<any>{
    return this.service.put(`${environment.rmsContextPath}/company/profile`, formData);
  }
  getData(): Observable<CompanyProfileModel> {
    return this.service.get(`${environment.rmsContextPath}/company/profile`);
  }
  getCompanyProfileImage(): Observable<any>{
    return this.service.getPhoto(`${environment.rmsContextPath}/company/profile/upload/view`);
  }
  uploadCompanyProfileImage( image: any): Observable<any>{
    return this.service.updatePhoto(`${environment.rmsContextPath}/company/profile/upload/logo`,image);
  }
}
