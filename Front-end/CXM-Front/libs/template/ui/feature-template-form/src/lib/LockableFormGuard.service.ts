import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanDeactivate,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import {
  ConfirmationMessage,
  ConfirmationMessageService,
} from '@cxm-smartflow/shared/ui/comfirmation-message';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

export interface ILockableForm {
  isLocked(): Observable<boolean>;
  getLockedReason(): string[];
}

@Injectable()
export class LockableFormGuardService implements CanDeactivate<ILockableForm> {
  canDeactivate(
    component: ILockableForm,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ):
    | boolean
    | UrlTree
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree> {
    return component
      .isLocked()
      .pipe(
        switchMap((islocked) =>
          islocked
            ? this.validateMessage(component.getLockedReason(), '')
            : of(!islocked)
        )
      );
  }

  validateMessage = (params: any, title: string) => {
    const confirmationData: ConfirmationMessage = {
      icon: 'error_outline',
      title: params[0],
      message: params[1],
      cancelButton: params[3],
      confirmButton: params[2],
      type: 'Warning',
    };
    return this.confirmService.showConfirmationPopup(confirmationData);
  };

  constructor(private confirmService: ConfirmationMessageService) {}
}
