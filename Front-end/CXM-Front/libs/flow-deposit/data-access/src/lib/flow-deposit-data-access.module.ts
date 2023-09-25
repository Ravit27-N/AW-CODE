import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import * as fileUploadFeature from './stores/file-upload-feature';
import * as navigateFeature from './stores/flow-tab-menu';
import * as depositFlowFeature from './stores/flow-deposit';
import * as depositFlowStepFeature from './stores/flow-deposit-step';
import * as flowDepositListFeature from './stores/flow-deposit-list';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { FlowDepositResolverService } from './services/flow-deposit-resolver-service';
import { FlowDepositControlService } from './services/flow-deposit-control.service';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { watermarkform } from '../lib/stores/watermark-form'
import { FlowDepositUiSettingOptionPopupModule } from '@cxm-smartflow/flow-deposit/ui/setting-option-popup';
import { FlowDepositSettingOptionEffect, flowDepositSettingOptionKey, flowDepositSettingOptionReducer } from './stores';


@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(fileUploadFeature.acquisitionFileUploadFeatureKey, fileUploadFeature.acquisitionFileUploadFeatureReducer),
    StoreModule.forFeature(navigateFeature.featureFlowTabMenuKey, navigateFeature.featureTabMenuReducer),
    StoreModule.forFeature(depositFlowFeature.flowDepositFeatureKey, depositFlowFeature.flowDepositFeatureReducer),
    StoreModule.forFeature(depositFlowStepFeature.featureFlowDepositStepKey, depositFlowStepFeature.flowDepositStepReducer),
    StoreModule.forFeature(flowDepositListFeature.flowDepositListKey, flowDepositListFeature.flowDepositListReducer),
    StoreModule.forFeature(flowDepositSettingOptionKey, flowDepositSettingOptionReducer),
    StoreModule.forFeature(watermarkform.watermarkformKey, watermarkform.reducer),
    StoreModule.forFeature(depositFlowFeature.depositBackground.featureKey, depositFlowFeature.depositBackground.reducer),
    EffectsModule.forFeature([
      fileUploadFeature.AcquisitionFileUploadEffect,
      navigateFeature.TabMenuEffect,
      depositFlowFeature.FlowDepositEffect,
      depositFlowStepFeature.FlowDepositStepEffect,
      flowDepositListFeature.FlowDepositListEffect,
      watermarkform.WatermarkFormEffect,
      FlowDepositSettingOptionEffect,
      depositFlowFeature.depositBackground.DepositFlowBackgroundList
    ]),
    StoreDevtoolsModule.instrument(),
    SharedDataAccessServicesModule,
    SharedTranslateModule.forRoot(),
    FlowDepositUiSettingOptionPopupModule,
  ],
  providers: [
    FlowDepositResolverService,
    FlowDepositControlService
  ]
})
export class FlowDepositDataAccessModule {}
