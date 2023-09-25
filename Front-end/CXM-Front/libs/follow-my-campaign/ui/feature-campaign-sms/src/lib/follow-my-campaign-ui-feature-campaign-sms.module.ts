import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Route, RouterModule } from '@angular/router';
import { SmsDestinationComponent } from './sms-destination/sms-destination.component';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedUiPaginatorModule } from '@cxm-smartflow/shared/ui/paginator';
import { FollowMyCampaignUiPreviewEmailTemplateModule } from '@cxm-smartflow/follow-my-campaign/ui/preview-email-template';
import {
  NgxMatDatetimePickerModule,
  NgxMatNativeDateModule,
  NgxMatTimepickerModule,
} from '@angular-material-components/datetime-picker';
import { CdkTableModule } from '@angular/cdk/table';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import {
  CampaignLockableFormGuardService,
  CampaignSmsRouteResolver,
  FollowMyCampaignDataAccessModule,
} from '@cxm-smartflow/follow-my-campaign/data-access';
import { SharedDirectivesCanModificationModule } from '@cxm-smartflow/shared/directives/can-modification';
import { SharedDirectivesCanAccessModule } from '@cxm-smartflow/shared/directives/can-access';
import { SharedUiProgressionLineModule } from '@cxm-smartflow/shared/ui/progression-line';
import { FollowMyCampaignUiFeatureCsvUploadModule } from '@cxm-smartflow/follow-my-campaign/ui/feature-csv-upload';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SmsParameterNewComponent } from './sms-parameter-new/sms-parameter-new.component';
import { SmsEnvoyComponent } from './sms-envoy/sms-envoy.component';
import { SmsSuccessPageComponent } from './sms-success-page/sms-success-page.component';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { SharedUiCxmDatetimePickerModule } from '@cxm-smartflow/shared/ui/cxm-datetime-picker';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { SharedDirectivesInfoTooltipModule } from '@cxm-smartflow/shared/directives/info-tooltip';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { SharedUiFormInputSelectionModule } from '@cxm-smartflow/shared/ui/form-input-selection';

const routes: Route[] = [
  {
    path: 'destination',
    canDeactivate: [CampaignLockableFormGuardService],
    component: SmsDestinationComponent,
    resolve: {
      loaded: CampaignSmsRouteResolver,
    },
    data: {
      breadcrumb: getBreadcrumb().campaign.campaignList,
    },
  },
  {
    path: 'parameter/:id',
    canDeactivate: [CampaignLockableFormGuardService],
    component: SmsParameterNewComponent,
    resolve: {
      loaded: CampaignSmsRouteResolver,
    },
    data: {
      breadcrumb: getBreadcrumb().campaign.campaignList,
    },
  },
  {
    path: 'envoy',
    canDeactivate: [CampaignLockableFormGuardService],
    component: SmsEnvoyComponent,
    resolve: {
      loaded: CampaignSmsRouteResolver,
    },
    data: {
      breadcrumb: getBreadcrumb().campaign.campaignList,
    },
  },
  {
    path: 'envoy/:id',
    canDeactivate: [CampaignLockableFormGuardService],
    component: SmsEnvoyComponent,
    resolve: {
      loaded: CampaignSmsRouteResolver,
    },
    data: {
      breadcrumb: getBreadcrumb().campaign.campaignList,
    },
  },
  {
    path: 'done',
    component: SmsSuccessPageComponent,
  },
];

@NgModule({
  imports: [
    CommonModule,
    CdkTableModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule.forChild(routes),
    SharedUiSpinnerModule,
    MaterialModule,
    FollowMyCampaignDataAccessModule,
    FollowMyCampaignUiPreviewEmailTemplateModule,
    SharedUiPaginatorModule,
    NgxMatDatetimePickerModule,
    NgxMatTimepickerModule,
    NgxMatNativeDateModule,
    SharedDirectivesTooltipModule,
    SharedDirectivesCanModificationModule,
    SharedDirectivesCanAccessModule,
    SharedUiProgressionLineModule,
    FollowMyCampaignUiFeatureCsvUploadModule,
    SharedCommonTypoModule,
    NgDynamicBreadcrumbModule,
    SharedUiCxmDatetimePickerModule,
    SharedDirectivesInfoTooltipModule,
    SharedTranslateModule.forRoot(),
    SharedUiFormInputSelectionModule,
    SharedPipesModule
  ],
  declarations: [
    SmsDestinationComponent,
    SmsParameterNewComponent,
    SmsEnvoyComponent,
    SmsSuccessPageComponent
  ],
})
export class FollowMyCampaignUiFeatureCampaignSmsModule {}
