import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SharedChatModule } from '@cxm-smartflow/shared/chat';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { FollowMyCampaignComponent } from './follow-my-campaign/follow-my-campaign.component';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedUiStepperModule } from '@cxm-smartflow/shared/ui/stepper';
import {
  CampaignLockableFormGuardService,
  CampaignSmsRouteResolver,
  FollowMyCampaignDataAccessModule,
} from '@cxm-smartflow/follow-my-campaign/data-access';
import { SharedUiComfirmationMessageModule } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { SharedUiStepModule } from '@cxm-smartflow/shared/ui/step';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SharedChatModule,
    MaterialModule,
    SharedUiButtonModule,
    SharedCommonTypoModule,
    NgDynamicBreadcrumbModule,
    SharedUiStepperModule,
    FollowMyCampaignDataAccessModule,
    SharedUiComfirmationMessageModule,
    SharedUiStepModule,
    SharedUiSpinnerModule,
    RouterModule.forChild([
      {
        path: '',
        component: FollowMyCampaignComponent,
        children: [
          {
            path: '',
            pathMatch: 'full',
            redirectTo: 'campaign-list'
          },
          {
            path: 'campaign-list',
            data: {
              breadcrumb: getBreadcrumb().campaign.campaignList
            },
            loadChildren: () => import('@cxm-smartflow/follow-my-campaign/ui/feature-list-campaign')
              .then(m => m.FollowMyCampaignUiFeatureListCampaignModule)
          },
          {
            path: 'email',
            loadChildren: () => import('@cxm-smartflow/follow-my-campaign/ui/feature-campaign-mail')
              .then(m => m.FollowMyCampaignUiFeatureCampaignMailModule)
          },
          {
            path: 'sms',
            data: {
              breadcrumb: getBreadcrumb().campaign.createCampaignSms
            },
            loadChildren: () => import('@cxm-smartflow/follow-my-campaign/ui/feature-campaign-sms').then(m => m.FollowMyCampaignUiFeatureCampaignSmsModule)
          },
          {
            path: 'campaign',
            loadChildren: () => import('@cxm-smartflow/follow-my-campaign/ui/feature-campaign').then(m => m.FollowMyCampaignUiFeatureCampaignModule)
          }
        ]
      }
    ]),
    StoreDevtoolsModule.instrument(),
    // For Translation
    HttpClientModule,
    SharedTranslateModule.forRoot(),
  ],
  declarations: [FollowMyCampaignComponent],
  exports: [FollowMyCampaignComponent],
  providers: [
    CampaignSmsRouteResolver,
    CampaignLockableFormGuardService
  ]
})
export class FollowMyCampaignFeatureModule {
}
