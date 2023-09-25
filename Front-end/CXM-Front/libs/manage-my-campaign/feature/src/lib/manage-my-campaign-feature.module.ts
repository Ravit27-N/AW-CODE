import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { EffectsModule } from '@ngrx/effects';
import { SharedUiCardSideBarModule } from '@cxm-smartflow/shared/ui/card-side-bar';
import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ManageMyCampaignComponent } from './manage-my-campaign/manage-my-campaign.component';
import {setFeaturedSettingFeatureKey, setFeaturedSettingReducer,SetFeaturedSettingEffect } from '@cxm-smartflow/manage-my-campaign/data-access';
import { AuthDataAccessModule } from '@cxm-smartflow/auth/data-access';
import { StoreModule } from '@ngrx/store';

@NgModule({
  imports: [
    CommonModule,
    AuthDataAccessModule.forRoot(),
    RouterModule.forChild([
      {
        path: '',
        component: ManageMyCampaignComponent,
        children: [
          {
            path: '',
            redirectTo: 'model',
            pathMatch: 'full',
          },
          {
            path: 'model',
            loadChildren: () =>
              import('@cxm-smartflow/manage-my-campaign/ui/feature-model').then(
                (m) => m.ManageMyCampaignUiFeatureModelModule
              ),
          },
          {
            path: 'setting',
            loadChildren: () =>
              import(
                '@cxm-smartflow/manage-my-campaign/ui/feature-setting'
              ).then((m) => m.ManageMyCampaignUiFeatureSettingModule),
          },
          {
            path: 'destination',
            loadChildren: () =>
              import(
                '@cxm-smartflow/manage-my-campaign/ui/feature-destination'
              ).then((m) => m.ManageMyCampaignUiFeatureDestinationModule),
          },
          {
            path: 'summary',
            loadChildren: () =>
              import(
                '@cxm-smartflow/manage-my-campaign/ui/feature-summary'
              ).then((m) => m.ManageMyCampaignUiFeatureSummaryModule),
          },
          {
            path: 'show-comfirmation',
            loadChildren: () =>
              import(
                '@cxm-smartflow/manage-my-campaign/ui/show-comfirmation'
              ).then((m) => m.ManageMyCampaignUiShowComfirmationModule),
          },
        ],
      },
    ]),
    StoreModule.forFeature(setFeaturedSettingFeatureKey, setFeaturedSettingReducer),
    EffectsModule.forFeature([SetFeaturedSettingEffect]),
    StoreDevtoolsModule.instrument(),
    SharedUiCardSideBarModule,
  ],
  declarations: [ManageMyCampaignComponent],
})

export class ManageMyCampaignFeatureModule {}
