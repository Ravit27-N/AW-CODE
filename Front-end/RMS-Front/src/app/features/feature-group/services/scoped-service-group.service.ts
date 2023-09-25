import { Injectable } from '@angular/core';
import { ApiService, IEnvelope, UserGroup, UserPayload } from '../../../core';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { EntityResponseHandler } from '../../../shared';
import { ScopedModelGroupListItem } from '../models/scoped-model-group-list.model';

@Injectable({
  providedIn: 'root',
})
export class ScopedServiceGroupService {
  constructor(private service: ApiService) {}

  getAllGroups(
    httpParams: HttpParams,
  ): Observable<EntityResponseHandler<ScopedModelGroupListItem>> {
    return this.service.get(`${environment.rmsContextPath}/group`, httpParams);
  }

  getById(id: string): Observable<UserGroup> {
    return this.service.get(`${environment.rmsContextPath}/group/${id}`);
  }

  createGroup(group: UserGroup): Observable<any> {
    return this.service.post(`${environment.rmsContextPath}/group`, group);
  }

  updateGroup(group: UserGroup): Observable<any> {
    return this.service.put(`${environment.rmsContextPath}/group/`, group);
  }

  active(id: number, state: boolean): Observable<any> {
    return this.service.patch(
      `${environment.rmsContextPath}/group/${id}/active/${state}`,
    );
  }

  deleteGroupById(groupId: number): Observable<any> {
    return this.service.delete(
      `${environment.rmsContextPath}/group/${groupId}`,
    );
  }

  assignRole(roleName: string, group: UserGroup): Observable<any> {
    return this.service.post(
      `${environment.rmsContextPath}/group/${group.id}/client-role/${roleName}`,
    );
  }

  resignRole(roleName: string, group: UserGroup): Observable<any> {
    return this.service.delete(
      `${environment.rmsContextPath}/group/${group.id}/client-role/${roleName}`,
    );
  }

  getMembers(group: UserGroup): Observable<IEnvelope<UserPayload>> {
    return this.service.get(
      `${environment.rmsContextPath}/group/${group.id}/view/member`,
    );
  }
}
