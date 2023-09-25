import { Injectable } from '@angular/core';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { Observable } from 'rxjs';
import {
  ActivatedRouteSnapshot,
  CanDeactivate,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Store } from '@ngrx/store';
import { take } from 'rxjs/operators';
import { removeUploadedFile, unloadClientForm } from '../store/modification/modification.actions';

export interface Confirmable {
  isLocked(): Observable<boolean>;
  confirmMsg?: string;
}

@Injectable({
  providedIn: 'root',
})
export class ClientControlService implements CanDeactivate<Confirmable> {
  constructor(
    private readonly confirmMsgService: ConfirmationMessageService,
    private readonly translateService: TranslateService,
    private readonly store: Store
  ) {}

  canDeactivate(
    component: Confirmable,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ):
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    return this.controlConfirmation(component.isLocked(), component?.confirmMsg);
  }

  private async controlConfirmation(
    isLocked: Observable<boolean>,
    confirmMsg?: string
  ): Promise<boolean> {
    const locked = await isLocked.pipe(take(1)).toPromise();
    if (!locked) return true;
    const confirmedLeave = await this.confirmLeave(confirmMsg);
    if (confirmedLeave) {
      this.store.dispatch(removeUploadedFile());
      this.store.dispatch(unloadClientForm());
    }
    return confirmedLeave;
  }

  private async confirmLeave(confirmMsg?: string): Promise<boolean> {

    const messageRef = confirmMsg? confirmMsg : 'client.confirmLeave';

    const { confirmButton, cancelButton, message, title } =
      await this.translateService.get(messageRef).toPromise();
    return this.confirmMsgService
      .showConfirmationPopup({
        icon: 'close',
        confirmButton,
        cancelButton,
        message,
        title,
        type: 'Warning',
      })
      .toPromise();
  }
}
