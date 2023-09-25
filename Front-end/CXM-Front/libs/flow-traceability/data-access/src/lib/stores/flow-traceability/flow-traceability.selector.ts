import { featureFlowTraceabilityKey } from './flow-traceability.reducer';
import { createFeatureSelector, createSelector } from '@ngrx/store';

const flowTraceabilityFeature = createFeatureSelector(
  featureFlowTraceabilityKey
);

export const selectSubChannelState = createSelector(
  flowTraceabilityFeature,
  (state: any) => state.subChannel
);

export const selectFlowStatusState = createSelector(
  flowTraceabilityFeature,
  (state: any) => state.filterCriteria?.flowStatus
);

export const selectDepositModeState = createSelector(
  flowTraceabilityFeature,
  (state: any) => state.filterCriteria?.depositMode
);

export const selectCriteriaSubChannelState = createSelector(
  flowTraceabilityFeature,
  (state: any) => state.filterCriteria?.subChannel
);

export const selectAllUsers = createSelector(
  flowTraceabilityFeature,
  (state: any) => state.users
);

export const selectSwitchTabToDocument = createSelector(
  flowTraceabilityFeature,
  (state: any) => state.isTabToDocument
);

export const selectFlowDocumentSubChannelState = createSelector(
  flowTraceabilityFeature,
  (state: any) => state.documentFilterCriteria?.sendingChannel
);

export const selectFlowDocumentStatusState = createSelector(
  flowTraceabilityFeature,
  (state: any) => state.documentFilterCriteria?.flowDocumentStatus
);
