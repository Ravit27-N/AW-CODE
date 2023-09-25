import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { IEnvelope } from '../model';
import { UserPayload } from '../model';
import { UserGroup } from '../model';
import { ApiService } from './api.service';
import {environment} from '../../../environments/environment';

@Injectable()
export class UserGroupAdminService {

  constructor(private service: ApiService) { }

  get(index: number, size: number): Observable<IEnvelope<UserGroup>> {
    let params = new HttpParams();
    if (size > 0) { params = params.set('pageSize', size.toString()); }
    if (index > 0) { params = params.set('page', index.toString()); }

    return this.service.get(`${environment.rmsContextPath}/group`, params);
  }

  getById(id: string): Observable<UserGroup> {
    return this.service.get(`${environment.rmsContextPath}/group/${id}`);
  }

  create(group: UserGroup): Observable<any> {
    return this.service.post(`${environment.rmsContextPath}/group`, group);
  }

  update(group: UserGroup): Observable<any> {
    return this.service.put(`${environment.rmsContextPath}/group/`, group);
  }

  active(id: number, state: boolean): Observable<any> {
    return this.service.patch(`${environment.rmsContextPath}/group/${id}/active/${state}`);
  }

  delete(group: UserGroup): Observable<any> {
    return this.service.delete(`${environment.rmsContextPath}/group/${group.id}`);
  }

  asignRole(roleName: string, group: UserGroup): Observable<any> {
    return this.service.post(`${environment.rmsContextPath}/group/${group.id}/client-role/${roleName}`);
  }

  resignRole(roleName: string, group: UserGroup): Observable<any> {
    return this.service.delete(`${environment.rmsContextPath}/group/${group.id}/client-role/${roleName}`);
  }

  getMembers(group: UserGroup): Observable<IEnvelope<UserPayload>> {
    return this.service.get(`${environment.rmsContextPath}/group/${group.id}/view/member`);
  }
}
