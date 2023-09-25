import { cxmProfileEnv as env } from '@env-cxm-profile';
import { Injectable } from '@angular/core';
import { clientModel, ListProfileCriteria, ProfileListModel, UserProfileModel } from '../models';
import { CxmProfileService } from '@cxm-smartflow/shared/data-access/api';
import { Observable, of } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { Params } from '@cxm-smartflow/shared/data-access/model';


@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  constructor(private cxmProfileService: CxmProfileService) { }

  getProfileList(
    params?: Params
  ): Observable<ProfileListModel>{
    let httpParams = new HttpParams();

    if(params) {
      Object.entries(params).forEach(([key, value]) => {
        httpParams = httpParams.set(key, value);
      });
    }

    return this.cxmProfileService.get(`${env.profileContext}/profiles`, httpParams);
  }

  deleteProfile(id: string): Observable<any>{
    return this.cxmProfileService.delete(`${env.profileContext}/profiles/${id}`);
  }

  createProfile(profile: UserProfileModel) {
    let params: any = profile;
    if (!profile?.clientId) {
      const {clientId, ...rest} = params;
      params = rest;
    }
    return this.cxmProfileService.post(`${env.profileContext}/profiles/create`, params)
  }

  getProfileById(id: string): Observable<UserProfileModel> {
    return this.cxmProfileService.get(`${env.profileContext}/profiles/${encodeURIComponent(id)}`);
  }

  updateProfile(profileId: string, profile: UserProfileModel) {
    let params: any = profile;
    if (!profile?.clientId) {
      const {clientId, ...rest} = params;
      params = rest;
    }

    return this.cxmProfileService.put(`${env.profileContext}/profiles/update`, {...params, id: profileId});
  }

  isProfileExist(profileName: string, clientId: number): Observable<boolean> {
    if(profileName?.trim()?.length > 0){
      if (clientId) {
        const params = new HttpParams().set('clientId', clientId);
        return this.cxmProfileService.get(`${env.profileContext}/profiles/duplicate/${profileName}`, params);
      } else {
        return this.cxmProfileService.get(`${env.profileContext}/profiles/duplicate/${profileName}`);
      }
    }
    return of(false);
  }

  getListUserOfProfile(profileId: string): Observable<string[]> {
    return this.cxmProfileService.get(`${env.profileContext}/profiles/${profileId}/users`)
  }

  getClients(): Observable<clientModel[]>{
    return this.cxmProfileService.get(`${env.profileContext}/clients`);
  }


  requestForgotPassword(email: string): Observable<any> {
    return this.cxmProfileService.post(`${env.profilePublicContext}/users/request/reset-password`, { email });
  }

  requestChangePassword(password: string, token: string): Observable<any> {
    return this.cxmProfileService.post(`${env.profilePublicContext}/users/reset-password`, {  password, token });
  }

  validateToken(token: string): Observable<boolean> {
    return this.cxmProfileService.get(`${env.profilePublicContext}/users/is-expired/${token}`);
  }

  getClientModule( param : { profileId: string, clientid: string }): Observable<string[]> {
    let params = new HttpParams();
    if(param.clientid) {
      params = params.append('clientId', param.clientid);
    }

    return this.cxmProfileService.get(`${env.profileContext}/profiles/get-functionalities-by-current-user`, params);
  }

  getClientCriteria(): Observable<ListProfileCriteria[]> {
    return this.cxmProfileService.get(`${env.profileContext}/clients/client-criteria`);
  }

}
