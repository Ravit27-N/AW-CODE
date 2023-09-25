import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import {
  FeatureFlowTraceabilityListEffect,
  featureFlowTraceabilityListKey,
  featureFlowTraceabilityListReducer,
} from './stores/flow-traceability-list';
import { EffectsModule } from '@ngrx/effects';
import {
  DocumentTraceabilityEffect,
  documentTraceabilityReducer,
  featureDocumentTraceabilityListKey,
} from './stores/document-traceability';

import {
  FeatureFlowTraceabilityEffect,
  featureFlowTraceabilityKey,
  featureFlowTraceabilityReducer,
} from './stores/flow-traceability';
import {
  featureFlowDetailKey,
  FlowDetailEffect,
  flowDetailReducer,
} from './stores/flow-detail';
import {
  featuredFlowDocumentDetailKey,
  FlowDocumentDetailEffect,
  flowDocumentDetailReducer,
} from './stores/flow-document-detail';
import { SharedUiComfirmationMessageModule } from '@cxm-smartflow/shared/ui/comfirmation-message';
import {
  FlowCriteriaSessionService,
  FlowDocumentShipmentControlService,
  FlowHandleExceptionService,
  FlowTraceabilityService,
} from './services';

import {
  featureClientFillerKey,
  featureFlowClientFillerReducer,
  FlowClientFillersEffect,
} from './stores/fillers/fillers';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';


import { flowNavDoc, FlowDocumentNavigatorEffect } from './stores/document-navigator';

@NgModule({
  imports: [
    CommonModule,
    SharedUiComfirmationMessageModule,
    StoreModule.forFeature(
      featureFlowTraceabilityListKey,
      featureFlowTraceabilityListReducer
    ),
    StoreModule.forFeature(
      featureDocumentTraceabilityListKey,
      documentTraceabilityReducer
    ),
    StoreModule.forFeature(featureFlowDetailKey, flowDetailReducer),
    StoreModule.forFeature(
      featureFlowTraceabilityKey,
      featureFlowTraceabilityReducer
    ),
    StoreModule.forFeature(
      featuredFlowDocumentDetailKey,
      flowDocumentDetailReducer
    ),
    StoreModule.forFeature(
      featureClientFillerKey,
      featureFlowClientFillerReducer
    ),
    StoreModule.forFeature(
      flowNavDoc.featureDocumentNavigatorKey,
      flowNavDoc.reducer
    ),

    EffectsModule.forFeature([
      FeatureFlowTraceabilityListEffect,
      DocumentTraceabilityEffect,
      FeatureFlowTraceabilityEffect,
      FlowDetailEffect,
      FlowDocumentDetailEffect,
      FlowClientFillersEffect,
      FlowDocumentNavigatorEffect
    ]),
    StoreDevtoolsModule.instrument(),
    SharedTranslateModule.forRoot(),
    SharedDataAccessServicesModule,
  ],
  providers: [
    FlowHandleExceptionService,
    FlowTraceabilityService,
    FlowCriteriaSessionService,
    FlowDocumentShipmentControlService,
  ],
})
export class FlowTraceabilityDataAccessModule {}
