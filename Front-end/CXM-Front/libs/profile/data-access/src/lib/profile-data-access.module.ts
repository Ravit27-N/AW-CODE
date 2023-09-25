import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { listProfileKey, listProfileReducer, ListProfileTableEffect } from './stores';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { SharedUiComfirmationMessageModule } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { featureManageProfileFormKey, manageProfileFormReducer, ProfileFormEffect, profileFormValidationFeaturekey, ProfileFormValidationEffect, manageProfileValidateFormReducer } from './stores/manage-profile';
import { TabMenuEffect, featureProfileTabMenuKey, featureTabMenuReducer } from './stores/profile-tab-menu';


@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(listProfileKey, listProfileReducer),
    StoreModule.forFeature(featureManageProfileFormKey, manageProfileFormReducer),
    StoreModule.forFeature(featureProfileTabMenuKey, featureTabMenuReducer),
    StoreModule.forFeature(profileFormValidationFeaturekey, manageProfileValidateFormReducer),
    EffectsModule.forFeature([
      ListProfileTableEffect,
      ProfileFormEffect,
      TabMenuEffect,
      ProfileFormValidationEffect
    ]),
    StoreDevtoolsModule.instrument(),
    SharedUiComfirmationMessageModule,
    SharedDataAccessServicesModule
  ],
})
export class ProfileDataAccessModule {}
