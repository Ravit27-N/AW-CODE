import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { StepOnActivated, unloadCampaignSms, unloadEmailCampaignFormData } from '@cxm-smartflow/follow-my-campaign/data-access';
import { appRoute, CampaignConstant } from '@cxm-smartflow/shared/data-access/model';
import { CanAccessibilityService } from '@cxm-smartflow/shared/data-access/services';
import { Store } from '@ngrx/store';

@Component({
  selector: 'cxm-smartflow-campaign-selection-page',
  templateUrl: './campaign-selection-page.component.html',
  styleUrls: ['./campaign-selection-page.component.scss']
})
export class CampaignSelectionPageComponent implements OnInit, OnDestroy {

  canCreateEmail: boolean;
  canCreateSms: boolean;

  constructor(private router: Router, private canAccess: CanAccessibilityService, private store: Store) {}

  toSms() {
    this.router.navigate([appRoute.cxmCampaign.followMyCampaign.smsCampaignChoiceOfModel]);
  }

  toEmailing() {
    this.router.navigate([appRoute.cxmCampaign.followMyCampaign.emailCampaign]);
  }

  ngOnInit(): void {
    // clear sms/email data before create new
    this.store.dispatch(unloadEmailCampaignFormData());
    this.store.dispatch(unloadCampaignSms());

    this.canCreateEmail = this.canAccess.canAccessible(CampaignConstant.CXM_CAMPAIGN, CampaignConstant.CREATE, true);
    this.canCreateSms = this.canAccess.canAccessible(CampaignConstant.CXM_CAMPAIGN_SMS, CampaignConstant.CREATE_SMS, true);

    this.store.dispatch(StepOnActivated({ active: true }));
  }

  ngOnDestroy(): void {
    this.store.dispatch(StepOnActivated({ active: false }));
  }

}
