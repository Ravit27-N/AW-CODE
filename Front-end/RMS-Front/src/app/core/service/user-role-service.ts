import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { FeatureModule, UserRoleModel } from '../model';
import { IEnvelope } from '../model';
import { HttpParams } from '@angular/common/http';
import { UserGroup } from '../model';
import { environment } from '../../../environments/environment';

@Injectable()
export class GroupService {
  constructor(private service: ApiService) {}

  get(index?: number, size?: number): Observable<IEnvelope<UserGroup>> {
    let params = new HttpParams();
    if (size > 0) {
      params = params.set('pageSize', size.toString());
    }
    if (index > 0) {
      params = params.set('page', index.toString());
    }

    return this.service.get(`${environment.rmsContextPath}/group`, params);
  }

  getById(groupId: number): Observable<UserGroup> {
    return this.service.get(`${environment.rmsContextPath}/group/${groupId}`);
  }

  update(groupId: number, group: UserGroup): Observable<any> {
    return this.service.put(
      `${environment.rmsContextPath}/group/${groupId}`,
      group,
    );
  }

  active(groupId: number, active: boolean): Observable<any> {
    return this.service.patch(
      `${environment.rmsContextPath}/group/${groupId}/active/${active}`,
    );
  }

  create(group: UserGroup): Observable<any> {
    return this.service.post(`${environment.rmsContextPath}/group`, group);
  }

  delete(groupId: number): Observable<any> {
    return this.service.delete(
      `${environment.rmsContextPath}/group/${groupId}`,
    );
  }
}

@Injectable()
export class ModuleService {
  constructor(private service: ApiService) {}

  get(index?: number, size?: number): Observable<IEnvelope<FeatureModule>> {
    let params = new HttpParams();
    params = params.set('pageSize', size.toString());
    if (index > 0) {
      params = params.set('page', index.toString());
    }

    return this.service.get(`${environment.rmsContextPath}/module`, params);
  }

  getById(id: number): Observable<FeatureModule> {
    return this.service.get(`${environment.rmsContextPath}/module/${id}`);
  }

  create(feature: FeatureModule): Observable<any> {
    return this.service.post(`${environment.rmsContextPath}/module`, feature);
  }

  update(id: number, feature: FeatureModule): Observable<any> {
    return this.service.put(`${environment.rmsContextPath}/module/`, feature);
  }

  active(id: number, state: boolean): Observable<any> {
    return this.service.patch(
      `${environment.rmsContextPath}/module/${id}/active/${state}`,
    );
  }

  delete(id: number): Observable<any> {
    return this.service.delete(`${environment.rmsContextPath}/module/${id}`);
  }
}

@Injectable()
export class RoleService {
  constructor(private service: ApiService) {}

  get(index?: number, size?: number): Observable<IEnvelope<UserRoleModel>> {
    let params = new HttpParams();
    if (size > 0) {
      params = params.set('pageSize', size.toString());
    }
    if (index > 0) {
      params = params.set('page', index.toString());
    }

    return this.service.get(`${environment.rmsContextPath}/user/role`, params);
  }

  getById(id: number): Observable<UserRoleModel> {
    return this.service.get(`${environment.rmsContextPath}/user/role/${id}`);
  }

  getByUser(username: string): Observable<UserRoleModel> {
    return this.service.get(
      `${environment.rmsContextPath}/user/role/user/${username}`,
    );
  }

  active(id: number, state: boolean): Observable<any> {
    return this.service.patch(
      `${environment.rmsContextPath}/user/role/${id}/active/${state}`,
    );
  }

  create(userRole: UserRoleModel): Observable<any> {
    return this.service.post(
      `${environment.rmsContextPath}/user/role`,
      userRole,
    );
  }

  update(userRole: UserRoleModel): Observable<any> {
    return this.service.put(
      `${environment.rmsContextPath}/user/role/${userRole.name}`,
      userRole,
    );
  }

  delete(userRoleName: string): Observable<any> {
    return this.service.delete(
      `${environment.rmsContextPath}/user/role/${userRoleName}`,
    );
  }
}

@Injectable()
export class UserGroupService {
  constructor(private service: ApiService) {}

  get(index?: number, size?: number): Observable<IEnvelope<UserGroup>> {
    let params = new HttpParams();
    if (size > 0) {
      params = params.set('pageSize', size.toString());
    }
    if (index > 0) {
      params = params.set('page', index.toString());
    }

    return this.service.get(`${environment.rmsContextPath}/user/group`, params);
  }

  create(userGroup: UserGroup): Observable<any> {
    return this.service.post(
      `${environment.rmsContextPath}/user/group`,
      userGroup,
    );
  }

  update(userGroup: UserGroup): Observable<any> {
    return this.service.put(
      `${environment.rmsContextPath}/user/group`,
      userGroup,
    );
  }

  delete(id: number): Observable<any> {
    return this.service.delete(
      `${environment.rmsContextPath}/user/group/${id}`,
    );
  }
}
