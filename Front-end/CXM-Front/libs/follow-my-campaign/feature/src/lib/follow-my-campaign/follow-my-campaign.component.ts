import {
  AfterContentChecked,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';

import { appRoute } from '@cxm-smartflow/shared/data-access/model';
import { TranslateService } from '@ngx-translate/core';
import { Store } from '@ngrx/store';
import {
  initLockableSmsForm,
  navigateToStep,
  selectCampaignEmailStep, selectEmailLoading, selectIsSMSloading,
  showStepSeq
} from '@cxm-smartflow/follow-my-campaign/data-access';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { IStep, StepperComponent } from '@cxm-smartflow/shared/ui/stepper';
import { Router } from '@angular/router';

@Component({
  selector: 'cxm-smartflow-follow-my-campaign',
  templateUrl: './follow-my-campaign.component.html',
  styleUrls: ['./follow-my-campaign.component.scss']
})

export class FollowMyCampaignComponent implements OnDestroy, OnInit, AfterViewInit, AfterContentChecked {
  routeProps = appRoute;
  steps$: Observable<any>;
  show$ = new BehaviorSubject(false);
  campaignHeader$ = new BehaviorSubject('');
  defaultHeader$ = new BehaviorSubject('');
  smsHeader$ = new BehaviorSubject('');
  emailHeader$ = new BehaviorSubject('');

  @ViewChild("stepper") stepper: StepperComponent;
  unsubscription$: Subscription ;
  isLoading$ = new Observable();
  isSMSLoading$ = new Observable();

  navigateTo(step: IStep) {
    this.store.dispatch(initLockableSmsForm({ isLock: false }));
    this.store.dispatch(navigateToStep({ step }));
  }


  constructor(private translate: TranslateService,
              private store: Store,
              private route: Router,
              private ref: ChangeDetectorRef) {
    this.steps$ = this.store.select(selectCampaignEmailStep);
    this.unsubscription$ = this.store.select(showStepSeq).subscribe(v => this.show$.next(v));
  }

  ngAfterViewInit(): void {
    this.ref.detectChanges();
  }

  ngOnDestroy(): void {
    this.unsubscription$?.unsubscribe();
    this.campaignHeader$?.unsubscribe();
    this.smsHeader$?.unsubscribe();
    this.emailHeader$?.unsubscribe();
    this.show$?.unsubscribe();
  }

  ngOnInit(): void {
    this.translate.get('cxmCampaign.followMyCampaign').subscribe((v) => {
      this.smsHeader$.next(v?.smsHeaderTitle);
      this.emailHeader$.next(v?.emailHeaderTitle);
      this.defaultHeader$.next(v?.campaignTitle);

      if (this.route?.url.toString()?.match('feature-campaign-list')?.length !== undefined) {
        this.campaignHeader$.next(this.defaultHeader$.value);
      } else if (this.route?.url.toString()?.match('sms')?.length !== undefined) {
        this.campaignHeader$.next(this.smsHeader$.value);
      } else {
        this.campaignHeader$.next(this.emailHeader$.value);
      }
    });

    this.isLoading$ = this.store.select(selectEmailLoading);
    this.isSMSLoading$ = this.store.select(selectIsSMSloading);
  }

  routeOutletEvent(event: Event) {
    if (this.route?.url.toString()?.match('feature-campaign-list')?.length !== undefined) {
      this.campaignHeader$.next(this.defaultHeader$.value);
    } else if (this.route?.url.toString()?.match('sms')?.length !== undefined) {
      this.campaignHeader$.next(this.smsHeader$.value);
    } else {
      this.campaignHeader$.next(this.emailHeader$.value);
    }
  }


  ngAfterContentChecked() {
    this.ref.detectChanges();
  }
}
