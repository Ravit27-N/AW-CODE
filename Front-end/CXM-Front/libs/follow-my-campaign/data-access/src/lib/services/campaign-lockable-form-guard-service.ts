import { BehaviorSubject, Observable, of } from 'rxjs';
import { ActivatedRoute, ActivatedRouteSnapshot, CanDeactivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Injectable, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { selectLockAbleForm, selectSmsCampaign, selectStep, selectStep2Lockable } from '../store/feature-campaign-sms';
import { TranslateService } from '@ngx-translate/core';
import { CampaignModel } from '../models';
import { FollowMyCampaignService } from './follow-my-campaign.service';
import { map, take } from 'rxjs/operators';
import { appRoute } from '@cxm-smartflow/shared/data-access/model';
import { StepOnActivated } from "../store/feature-campaign-email";

@Injectable({ providedIn: 'root' })
export class CampaignLockableFormGuardService implements CanDeactivate<any>, OnDestroy {

  labelProp: any;
  step$ = new BehaviorSubject(1);
  campaign$ = new BehaviorSubject<CampaignModel>({ type: 'SMS' });
  fileIdFromUrl$ = new BehaviorSubject('');
  isLockForm$ = new BehaviorSubject(true);

  constructor(private activatedRoute: ActivatedRoute, private store: Store, private confirmMessage: ConfirmationMessageService, private translate: TranslateService, private service: FollowMyCampaignService) {
    this.translate.get('cxmCampaign.followMyCampaign.lockable-form.popup.message').subscribe(v => this.labelProp = v);

    this.activatedRoute.queryParams.subscribe(params => {
      const param = { step: params['step'], fileId: params['fileId'] };
      this.fileIdFromUrl$.next(param?.fileId);
    });

    this.store.select(selectStep).subscribe(v => this.step$.next(v));
    this.store.select(selectSmsCampaign).subscribe(campaign => this.campaign$.next(campaign));
    this.store.select(selectLockAbleForm).subscribe(v => this.isLockForm$.next(v));
  }

  canDeactivate(component: any, currentRoute: ActivatedRouteSnapshot, currentState: RouterStateSnapshot, nextState?: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (location.pathname.includes(appRoute.cxmCampaign.followMyCampaign.smsCampaignDestination)) {
      return this.validStep2(nextState?.url);
    } else if (location.pathname.includes(appRoute.cxmCampaign.followMyCampaign.smsCampaignParameter) &&
      nextState?.url.includes(appRoute.cxmCampaign.followMyCampaign.smsCampaignDestination)) {
      return true;
    }

    return this.isLockForm$.value ? this.validate() : of(true);
  }

  private async validStep2(currURL: any): Promise<boolean> {
    const exceptRoutes = [
      appRoute.cxmCampaign.followMyCampaign.smsCampaignDestination,
      appRoute.cxmCampaign.followMyCampaign.smsCampaignParameter,
      appRoute.cxmCampaign.followMyCampaign.smsCampaignEnvoy,
    ];

    const validRoute = exceptRoutes.some(e => currURL?.includes(e));
    if (validRoute) return true;

    const isValid = await this.store.select(selectStep2Lockable).pipe(take(1)).toPromise();
    return isValid? this.validate().toPromise(): true;
  }

  private validate(): Observable<boolean> {
    const response = this.confirmMessage.showConfirmationPopup({
      title: this.labelProp?.heading,
      message: this.labelProp?.message,
      cancelButton: this.labelProp?.cancelBtn,
      confirmButton: this.labelProp?.confirmBtn,
      type: 'Warning'
    });

    response.pipe(take(1))?.subscribe(value => {
      if (value) {
        if (this.step$.value === 3 || this.step$.value === 4) {
          this.updateStepByCampaignProcess(this.step$.value);
          this.store.dispatch(StepOnActivated({ active: false, leave: true }));
        }
      }
    });

    return response;
  }

  updateStepByCampaignProcess(step: number) {
    if (this.campaign$.value?.fileId !== undefined) {
      this.service.updateStepByCampaignProcess(this.campaign$.value?.fileId, step, this.campaign$.value?.type)
        .subscribe(res => console.log());
    }
  }

  ngOnDestroy(): void {
    this.step$.unsubscribe();
    this.campaign$.unsubscribe();
  }
}
