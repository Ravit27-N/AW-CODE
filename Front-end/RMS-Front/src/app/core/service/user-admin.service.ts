import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { IEnvelope, UserPayload } from '../model';
import { ApiService } from './api.service';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserAdminService {
  constructor(private service: ApiService) {}

  update(user: UserPayload): Observable<any> {
    return this.service.put(`${environment.rmsContextPath}/user`, user);
  }

  create(user: UserPayload): Observable<any> {
    return this.service.post(`${environment.rmsContextPath}/user`, user);
  }

  get(
    index?: number,
    size?: number,
    sortDirection?: string,
    sortByField?: string,
    filter?: string,
  ): Observable<IEnvelope<UserPayload>> {
    let params = new HttpParams();
    if (size > 0) {
      params = params.set('pageSize', size.toString());
    }
    if (index > 0) {
      params = params.set('page', index.toString());
    }
    if (filter.length) {
      params = params.set('filter', filter);
    }
    if (sortDirection.length) {
      params = params.set('sortDirection', sortDirection);
    }
    if (sortByField.length) {
      params = params.set('sortByField', sortByField);
    }

    return this.service.get(`${environment.rmsContextPath}/user`, params);
  }

  getById(userId: string): Observable<any> {
    return this.service.get(`${environment.rmsContextPath}/user/${userId}`);
  }

  delete(userId: string): Observable<any> {
    return this.service.delete(`${environment.rmsContextPath}/user/${userId}`);
  }

  joinGroup(roleName: string, groupId: string): Observable<any> {
    return this.service.put(
      `${environment.rmsContextPath}/user/username/${roleName}/groups/${groupId}`,
    );
  }

  leaveGroup(roleName: string, groupId: string): Observable<any> {
    return this.service.delete(
      `${environment.rmsContextPath}/user/username/${roleName}/groups/${groupId}`,
    );
  }

  setUserPassword(user: UserPayload, password: string, istemp: boolean) {
    return this.service.put(
      `${environment.rmsContextPath}/user/${user.id}/password`,
      {
        password,
        temporary: istemp,
      },
    );
  }

  changeStatus(userId: string, enable: boolean) {
    return this.service.patch(
      `${environment.rmsContextPath}/user/${userId}/enabled/${enable}`,
    );
  }

  validateEmail(email: string): Observable<boolean> {
    return this.service.get(
      `${environment.rmsContextPath}/user/validateEmail/${email}`,
    );
  }

  validateUsername(username: string): Observable<boolean> {
    return this.service.get(
      `${environment.rmsContextPath}/user/validateUsername/${username}`,
    );
  }
}
