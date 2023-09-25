import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import {
  ManageAnalyticsEffect,
  manageAnalyticsReducer,
  manageAnalyticsReducerKey,
} from './stores';
import { SharedDataAccessApiModule } from '@cxm-smartflow/shared/data-access/api';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';

@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(manageAnalyticsReducerKey, manageAnalyticsReducer),
    EffectsModule.forFeature([ManageAnalyticsEffect]),
    StoreDevtoolsModule.instrument(),
    SharedTranslateModule.forRoot(),
    SharedDataAccessApiModule,
    SharedDataAccessServicesModule,
  ],
})
export class AnalyticsDataAccessModule {}
