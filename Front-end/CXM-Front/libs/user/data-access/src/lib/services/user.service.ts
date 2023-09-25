import { Injectable } from '@angular/core';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { CxmProfileService, CxmServiceService, CxmUserService } from '@cxm-smartflow/shared/data-access/api';
import { cxmProfileEnv as env } from '@env-cxm-profile';
import {
  BatchUserResponse,
  ClientCriteria,
  ClientResponse,
  CreateUserRequestModel,
  ProfileAssigned,
  UpdateUserRequestModel, UserDetail, UserList,
  UserModel
} from '../models';
import { Params } from '@cxm-smartflow/shared/data-access/model';
import { HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { removeFalsyObject } from '@cxm-smartflow/shared/utils';
import { UserUtil } from '@cxm-smartflow/shared/data-access/services';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  isAdmin = UserUtil.isAdmin();

  constructor(private cxmUserService: CxmUserService, private cxmServiceService: CxmServiceService,
              private cxmProfileService: CxmProfileService) {
  }

  /**
   * this method used to get list of users
   * @param params refer to pagination and filtering parameter of {@link Params}
   */
  getUserList(params: Params): Observable<UserList> {
    let params$ = new HttpParams();
    if (params !== undefined) {
      Object.entries(params).forEach(([key, value]) => {
        params$ = params$.set(key, value);
      });
    }

    return this.cxmUserService.get(`${env.profileContext}/users`, params$);
  }

  /**
   * this method used to check is duplicated email
   * @param email refer to prefer target email
   */
  checkIsDuplicatedEmail(email: string): Observable<boolean> {
    return this.cxmUserService.get(`${env.profileContext}/users/${email}/available`);
  }


  getAllProfilePage(page: number, pageSize: number) {
    const params = new HttpParams().set('page', page).set('pageSize', pageSize);

    return this.cxmUserService.get(`${env.profileContext}/profiles`, params);
  }

  /**
   * this method used to create a new user
   * @param user refer to {@link CreateUserModel}
   */
  createUser(user: CreateUserRequestModel) {
    return this.cxmUserService.post(`${env.profileContext}/users`, user);
  }

  /**
   * This method used to get user detail by id
   * @param singleModifiedUserId refer to id of {@link UserModel}
   */
  getUserById(singleModifiedUserId: string): Observable<UserDetail>{
    return this.cxmUserService.get(`${env.profileContext}/users/${singleModifiedUserId}`);
  }

  /**
   * This method used to update the list of user
   * @param userIds refers to list modified users id
   * @param profiles refers to list of assigned profile
   */
  updateBatchUser(userIds: string[], profiles: number[]): Observable<any> {
    const requestBody = { userIds, profiles };
    return this.cxmUserService.put(`${env.profileContext}/users/profiles/`, requestBody);
  }

  /**
   * this method used to update a single user
   * @param updateUserRequest
   */
  updateSingleUser(updateUserRequest: UpdateUserRequestModel) {
    const params = {  admin: updateUserRequest.admin, ...removeFalsyObject(updateUserRequest) };
    return this.cxmUserService.put(`${env.profileContext}/users`, params);
  }

  deleteUsers(userIds: string[]) {
    return this.cxmUserService.delete(`${env.profileContext}/users/${userIds}`);
  }

  getAllProfileByServiceId(serviceId: number): Observable<ProfileAssigned[]> {
    return this.cxmUserService.get(`${env.profileContext}/profiles/services/${serviceId}`);
}


  getClientCriteria(sortDirection?: string): Observable<ClientCriteria[]> {
    let param$ = new HttpParams();
    if (sortDirection) {
      param$ = param$.set('sortDirection', sortDirection);
    }
    return this.cxmProfileService.get(`${env.profileContext}/clients/client-criteria`, param$);
  }

  getClientService(clientId?: number): Observable<ClientResponse> {
    let param$ = new HttpParams();
    if (clientId) {
      param$ = param$.set('clientId', clientId);
    }

    return this.cxmProfileService.get(`${env.profileContext}/clients/services`, param$);
  }

  /**
   * this method used to get list of available services
   */
  getServiceList(): Observable<any> {
    return this.cxmServiceService.get(`${env.profileContext}/services/active`);
  }

    //add new
    getOrganizationProfiles(clientIds?: number): Observable<{ id: number, name: string }[]> {
      if(clientIds==undefined || clientIds==0){
      return this.cxmServiceService.get(`${env.profileContext}/profiles/criteria`);}
      return this.cxmServiceService.get(`${env.profileContext}/profiles/client/${clientIds}`);
    }
 /**
   * this method used to get list of available services
   */
 getServiceListClient(clientIds?: any,divisionIds?: any): Observable<any> {
  if(clientIds == 'true'){
    //NonAdmin
    return this.cxmServiceService.get(`${env.profileContext}/services/servicesInClientList`);
  }
if((clientIds==undefined || clientIds==0) && (divisionIds==undefined || divisionIds==0)){
    return this.cxmServiceService.get(`${env.profileContext}/services`);
  }
  if(divisionIds==''){
    let param$ = new HttpParams();
    param$ = param$.set('divisionId', 0);
    return this.cxmServiceService.get(`${env.profileContext}/services/client/${clientIds}`, param$);
  }
  if(clientIds==undefined){
    let param$ = new HttpParams();
    param$ = param$.set('divisionId', divisionIds);
    return this.cxmServiceService.get(`${env.profileContext}/services/client/0`, param$);
  }
  let param$ = new HttpParams();
    param$ = param$.set('divisionId', divisionIds);
  return this.cxmServiceService.get(`${env.profileContext}/services/client/${clientIds}`, param$);
}


 /**
 * this method used to get list of available division par client id
 */
 getClientDivision(clientIds?: any): Observable<any>  {
  if(clientIds == 'true'){
    //NonAdmin
    return this.cxmServiceService.get(`${env.profileContext}/divisions/divisionInClientList`);
  }
  if(clientIds==undefined || clientIds==0){
    return this.cxmServiceService.get(`${env.profileContext}/divisions`);
  }
  return this.cxmServiceService.get(
    `${env.profileContext}/divisions/client/${clientIds}`
  );
}

  validateImportedUserCSV(file: FormData): Observable<HttpResponse<BatchUserResponse>> {
   return this.cxmUserService.uploadFile(`${env.profileContext}/users/batch-users`, file);
  }

exportUsersToCSV(
  profileIds: number[],
  userType: string[],
  clientIds: number[],
  divisionIds: number[],
  serviceIds: number[],
  filter: string,
  filename: string
): Observable<any> {
  const params = new HttpParams()
    .set('profileIds', profileIds.length > 0 ? profileIds.join(',') : '')
    .set('userType', userType.length > 0 ? userType.join(',') : '')
    .set('clientIds', clientIds.length > 0 ? clientIds.join(',') : '')
    .set('divisionIds', divisionIds.length > 0 ? divisionIds.join(',') : '')
    .set('serviceIds', serviceIds.length > 0 ? serviceIds.join(',') : '')
    .set('filter', filter)
    .set('filename', filename);
  return this.cxmServiceService.fileCsv(`${env.profileContext}/users/export-users-csv`, params);
}

getUserService(userId?: number): Observable<ClientResponse> {
  let param$ = new HttpParams();
  if (userId) {
    param$ = param$.set('userId', userId);
  }
  return this.cxmProfileService.get(`${env.profileContext}/clients/user-services`, param$);
}


}
