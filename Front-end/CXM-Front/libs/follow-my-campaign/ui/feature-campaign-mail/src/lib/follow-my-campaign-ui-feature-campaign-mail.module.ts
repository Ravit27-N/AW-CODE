import {
  NgxMatDatetimePickerModule,
  NgxMatNativeDateModule,
  NgxMatTimepickerModule,
} from '@angular-material-components/datetime-picker';
import { CdkTableModule } from '@angular/cdk/table';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import {
  CampaignEmailLockableFormGuardService,
  FollowMyCampaignDataAccessModule,
} from '@cxm-smartflow/follow-my-campaign/data-access';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedUiImageModule } from '@cxm-smartflow/shared/ui/image';
import { SharedUiStepperModule } from '@cxm-smartflow/shared/ui/stepper';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { EmailDestinationComponent } from './email-destination/email-destination.component';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { MatDialogModule } from '@angular/material/dialog';
import { RejectedMailComponent } from './email-destination/rejected-mail/rejected-mail.component';
import { RejectedMailService } from './email-destination/rejected-mail/rejected-mail.service';
import { SharedUiPaginatorModule } from '@cxm-smartflow/shared/ui/paginator';
import { EmailTemplateParameterResolverService } from './email-parameter/email-parameter-resolver.service';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { SharedDirectivesCanModificationModule } from '@cxm-smartflow/shared/directives/can-modification';
import { SharedDirectivesCanAccessModule } from '@cxm-smartflow/shared/directives/can-access';
import { FollowMyCampaignUiFeatureCsvUploadModule } from '@cxm-smartflow/follow-my-campaign/ui/feature-csv-upload';
import { EmailParameterComponent } from './email-parameter/email-parameter.component';
import { EmailEnvoyComponent } from './email-envoy/email-envoy.component';

import { SharedUiProgressionLineModule } from '@cxm-smartflow/shared/ui/progression-line';
import { EmailSuccessPageComponent } from './email-success-page/email-success-page.component';
import { SharedUiCxmDatetimePickerModule } from '@cxm-smartflow/shared/ui/cxm-datetime-picker';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { SharedDirectivesInfoTooltipModule } from '@cxm-smartflow/shared/directives/info-tooltip';
import { EmailPreviewComponent } from './email-envoy/email-preview/email-preview.component';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { SharedUiFormInputSelectionModule } from '@cxm-smartflow/shared/ui/form-input-selection';

const routes: Routes = [
  {
    path: 'destination',
    canDeactivate: [CampaignEmailLockableFormGuardService],
    data: {
      breadcrumb: getBreadcrumb().campaign.campaignList
    },
    component: EmailDestinationComponent
  },
  {
    path: 'destination/:templateId',
    data: {
      breadcrumb: getBreadcrumb().campaign.campaignList
    },
    component: EmailDestinationComponent
  },
  {
    path: 'parameter',
    data: {
      breadcrumb: getBreadcrumb().campaign.campaignList
    },
    component: EmailParameterComponent
  },
  {
    path: 'parameter/:campaignId',
    canDeactivate: [CampaignEmailLockableFormGuardService],
    data: {
      breadcrumb: getBreadcrumb().campaign.campaignList
    },
    component: EmailParameterComponent,
    resolve: {
      allData: EmailTemplateParameterResolverService
    }
  },
  {
    path: 'envoi',
    canDeactivate: [CampaignEmailLockableFormGuardService],
    data: {
      breadcrumb: getBreadcrumb().campaign.campaignList
    },
    component: EmailEnvoyComponent
  },
  {
    path: 'envoi/:campaignId',
    canDeactivate: [CampaignEmailLockableFormGuardService],
    data: {
      breadcrumb: getBreadcrumb().campaign.campaignList
    },
    component: EmailEnvoyComponent,
    resolve: {
      allData: EmailTemplateParameterResolverService
    }
  },
  {
    path: 'done',
    component: EmailSuccessPageComponent
  }
];

@NgModule({
  imports: [
    CommonModule,
    SharedCommonTypoModule,
    ReactiveFormsModule,
    FollowMyCampaignDataAccessModule,
    SharedDirectivesTooltipModule,
    SharedUiStepperModule,
    FormsModule,
    MaterialModule,
    CdkTableModule,
    RouterModule.forChild(routes),
    HttpClientModule,
    NgDynamicBreadcrumbModule,
    StoreDevtoolsModule.instrument(),
    SharedUiImageModule,
    NgxMatDatetimePickerModule,
    NgxMatTimepickerModule,
    NgxMatNativeDateModule,
    SharedDataAccessServicesModule,
    MatDialogModule,
    SharedUiPaginatorModule,
    SharedUiSpinnerModule,
    SharedDirectivesCanModificationModule,
    SharedDirectivesCanAccessModule,
    FollowMyCampaignUiFeatureCsvUploadModule,
    SharedDirectivesInfoTooltipModule,
    SharedTranslateModule.forRoot(),
    SharedUiProgressionLineModule,
    SharedUiCxmDatetimePickerModule,
    SharedPipesModule,
    SharedUiFormInputSelectionModule,
    SharedCommonTypoModule
  ],
  declarations: [
    EmailDestinationComponent,
    RejectedMailComponent,
    EmailParameterComponent,
    EmailEnvoyComponent,
    EmailSuccessPageComponent,
    EmailPreviewComponent
  ],
  providers: [
    RejectedMailService,
    EmailTemplateParameterResolverService,
    CampaignEmailLockableFormGuardService
  ],

})
export class FollowMyCampaignUiFeatureCampaignMailModule {
}
