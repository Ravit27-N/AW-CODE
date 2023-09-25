import { Observable, of } from 'rxjs';
import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanDeactivate,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { TranslateService } from '@ngx-translate/core';
import { Store } from '@ngrx/store';
import { StepOnActivated, updateCampaignParameterWhenLeavePage } from "../store/feature-campaign-email";
import { appRoute } from '@cxm-smartflow/shared/data-access/model';

export interface ILockableForm {
  isLocked(): Observable<boolean>;

  getLockedReason(): string[];
}

@Injectable({ providedIn: 'root' })
export class CampaignEmailLockableFormGuardService implements CanDeactivate<ILockableForm> {
  private confirmMessage: ConfirmationMessageService;
  private labelProp: any;

  constructor(private confirmMessageService: ConfirmationMessageService, private translate: TranslateService, private store: Store) {
    this.confirmMessage = confirmMessageService;
    this.translate.get('cxmCampaign.followMyCampaign.lockable-form.popup.message').subscribe(v => this.labelProp = v);
  }

  canDeactivate(
    component: ILockableForm,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    return component.isLocked().pipe(
        switchMap((isLocked) => {
          const exceptRoutes = [
            appRoute.cxmCampaign.followMyCampaign.emailCampaignDestination,
            appRoute.cxmCampaign.followMyCampaign.emailCampaignParameter,
            appRoute.cxmCampaign.followMyCampaign.emailCampaignSummary,
            appRoute.cxmCampaign.followMyCampaign.emailChoiceOfModel,
          ];

          const validRoute = exceptRoutes.some(e => nextState?.url?.includes(e) && nextState?.url !== `/${appRoute.cxmCampaign.followMyCampaign.emailChoiceOfModel}`);
          if (validRoute) {
            return of(true);
          }

          return isLocked ? this.validate() : of(true);
        }));
  }

  private validate(): Observable<boolean> {
    const response = this.confirmMessage.showConfirmationPopup({
      title: this.labelProp?.heading,
      message: this.labelProp?.message,
      cancelButton: this.labelProp?.cancelBtn,
      confirmButton: this.labelProp?.confirmBtn,
      type: 'Warning'
    });

    // Remove attachments of email campaign parameter form.
    response.subscribe(yesNo => {
      if (yesNo){
        // this.store.dispatch(removeAttachmentUploadedWhenLeavePage());
        this.store.dispatch(updateCampaignParameterWhenLeavePage());
        this.store.dispatch(StepOnActivated({ active: false, leave: true }));
      }
    });

    return response;
  }
}
