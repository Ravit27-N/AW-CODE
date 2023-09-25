import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import {
  ManageResourceEffect,
  manageResourceReducer,
  manageResourceReducerKey,
} from './store';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(manageResourceReducerKey, manageResourceReducer),
    EffectsModule.forFeature([ManageResourceEffect]),
    StoreDevtoolsModule.instrument(),
    SharedDataAccessServicesModule,
    SharedTranslateModule.forRoot(),
  ],
})
export class SettingDataAccessModule {}
