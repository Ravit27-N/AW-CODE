import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Route, RouterModule } from '@angular/router';
import { CampaignSelectionPageComponent } from './campaign-selection-page/campaign-selection-page.component';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { CampaignModelSelectionPageComponent } from './campaign-model-selection-page/campaign-model-selection-page.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { GridListTemplateSelectionComponent } from './grid-list-template-selection/grid-list-template-selection.component';

import {
  FeatureListEmailTemplateEffect,
  featureListEmailTemplateFeatureKey,
  featureListTemplateReducer,
} from '@cxm-smartflow/template/data-access';
import { SharedUiImageModule } from '@cxm-smartflow/shared/ui/image';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { FollowMyCampaignDataAccessModule } from '@cxm-smartflow/follow-my-campaign/data-access';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { SharedUiSearchBoxModule } from '@cxm-smartflow/shared/ui/search-box';

export const followMyCampaignUiFeatureCampaignRoutes: Route[] = [
  {
    path: '',
    component: CampaignSelectionPageComponent,
    data: {
      breadcrumb: getBreadcrumb().campaign.campaignList,
    },
  },
  {
    path: 'emailing',
    component: CampaignModelSelectionPageComponent,
    data: {
      breadcrumb: getBreadcrumb().campaign.campaignList,
      campaignType: 'EMAILING',
    },
  },
  {
    path: 'sms',
    component: CampaignModelSelectionPageComponent,
    data: {
      breadcrumb: getBreadcrumb().campaign.campaignList,
      campaignType: 'SMS',
    },
  },
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(followMyCampaignUiFeatureCampaignRoutes),
    SharedCommonTypoModule,
    NgDynamicBreadcrumbModule,
    MaterialModule,
    SharedUiImageModule,

    // Required templtate date access module
    StoreModule.forFeature(
      featureListEmailTemplateFeatureKey,
      featureListTemplateReducer
    ),

    EffectsModule.forFeature([FeatureListEmailTemplateEffect]),
    StoreDevtoolsModule.instrument(),
    SharedDataAccessServicesModule,
    FollowMyCampaignDataAccessModule,
    SharedTranslateModule.forRoot(),
    SharedDirectivesTooltipModule,
    SharedUiSearchBoxModule,
  ],
  declarations: [
    CampaignSelectionPageComponent,
    CampaignModelSelectionPageComponent,
    GridListTemplateSelectionComponent,
  ],
})
export class FollowMyCampaignUiFeatureCampaignModule {}
