import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListCampaignComponent } from './list-campaign.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { RouterModule } from '@angular/router';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { SharedDirectivesCanVisibilityModule } from '@cxm-smartflow/shared/directives/can-visibility';
import { SharedDirectivesCanModificationModule } from '@cxm-smartflow/shared/directives/can-modification';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { FollowMyCampaignDataAccessModule } from '@cxm-smartflow/follow-my-campaign/data-access';
import { SharedUiComfirmationMessageModule } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { CampaignFilterComponentComponent } from './campaign-filter-component/campaign-filter-component.component';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { SharedUiSearchBoxModule } from '@cxm-smartflow/shared/ui/search-box';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    SharedTranslateModule.forRoot(),
    SharedUiSpinnerModule,
    SharedDirectivesCanVisibilityModule,
    SharedDirectivesCanModificationModule,
    SharedDataAccessServicesModule,
    FollowMyCampaignDataAccessModule,
    SharedUiComfirmationMessageModule,
    SharedDirectivesTooltipModule,
    SharedCommonTypoModule,
    NgDynamicBreadcrumbModule,
    SharedUiSearchBoxModule,
    RouterModule.forChild([{ path: '', component: ListCampaignComponent }]),
  ],
  declarations: [ListCampaignComponent, CampaignFilterComponentComponent],
  exports: [ListCampaignComponent],
})
export class FollowMyCampaignUiFeatureListCampaignModule {}
