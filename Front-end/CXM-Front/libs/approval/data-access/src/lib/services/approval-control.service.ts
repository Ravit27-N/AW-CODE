import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { appLocalStorageConstant, appRoute, EspaceValidation } from '@cxm-smartflow/shared/data-access/model';
import { CanModificationService } from '@cxm-smartflow/shared/data-access/services';
import { Router } from '@angular/router';
import { ApprovalDocModel } from '../models';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApprovalControlService {

  readonly USER_FILTERING = 'user_filtering';
  readonly DATE_FILTERING = 'date_filtering';
  readonly CHANNEL_FALTERING = 'channel_filtering';

  constructor(private readonly translate: TranslateService,
              private readonly canModificationService: CanModificationService,
              private readonly router: Router) {
    this.translate.use(localStorage.getItem(appLocalStorageConstant.Common.Locale.Locale) || appLocalStorageConstant.Common.Locale.Fr);
  }

  public checkIsCanValidateOrRefuse(ownerId: number): boolean {
    return this.canModificationService.hasModify(EspaceValidation.CXM_ESPACE_VALIDATION, EspaceValidation.VALIDATE_REFUSE, ownerId, true);
  }

  public saveUserFilteringInStorage(users: string[]) {
    localStorage.setItem(this.USER_FILTERING, JSON.stringify(users));
  }

  public saveDateFilteringInStorage(start: string, end: string) {
    localStorage.setItem(this.DATE_FILTERING, JSON.stringify({ start, end }));
  }

  public saveChannelFilteringInStorage(channel: string, categories: string[]) {
    localStorage.setItem(this.CHANNEL_FALTERING, JSON.stringify({ channel, categories }));
  }

  public clearFilteringInStorage() {
    localStorage.removeItem(this.USER_FILTERING);
    localStorage.removeItem(this.DATE_FILTERING);
    localStorage.removeItem(this.CHANNEL_FALTERING);
  }

  public navigateToFlowDocument(flowId: number): void {
    this.router.navigateByUrl(`${appRoute.cxmApproval.navigateToValidateFlowDocument}/${atob(flowId.toString())}`);
  }
}
