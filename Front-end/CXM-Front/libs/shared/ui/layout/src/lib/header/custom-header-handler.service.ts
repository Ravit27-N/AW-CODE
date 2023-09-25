import { Injectable } from '@angular/core';
import { LocalStorageService } from '@cxm-smartflow/follow-my-campaign/util';
import { AuthService } from '@cxm-smartflow/auth/data-access';
import { Observable, of } from 'rxjs';
import { UserValidation } from '@cxm-smartflow/dashboard/data-access';
import { ApiService } from '@cxm-smartflow/shared/data-access/api';

@Injectable({
  providedIn: 'root'
})
export class CustomHeaderHandlerService {

  constructor(
    private localStorageService: LocalStorageService,
    private api: ApiService,
    private authService: AuthService) {
  }

  public logout() {
    this.authService.logout();
  }

  public getUser(): Observable<string> {
    const userPrivileges = JSON.parse(<string>localStorage.getItem('userPrivileges'));
    if (userPrivileges?.name === null) {
      return of(<string>localStorage.getItem('user'));
    } else {
      return of(userPrivileges?.name);
    }
  }

  fetchValidationDocumentExists(): Observable<UserValidation> {
    return this.api.get('/cxm-flow-traceability/api/v1/validation/remaining');
  }
}
