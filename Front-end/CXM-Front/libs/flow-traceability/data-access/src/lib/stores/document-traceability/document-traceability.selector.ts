import { createFeatureSelector, createSelector } from '@ngrx/store';
import { featureDocumentTraceabilityListKey } from './document-traceability.reducer';

const featureDocumentTraceability = createFeatureSelector<any>(
  featureDocumentTraceabilityListKey
);

export const selectFlowDocumentList = createSelector(
  featureDocumentTraceability,
  (state) => state
);

export const selectFlowDocumentShowSearchBoxTooltip = createSelector(
  featureDocumentTraceability,
  (state) => state.showSearchBoxTooltip
);

export const selectDataOfViewShipment = createSelector(
  featureDocumentTraceability,
  (state) => ({
    flowTraceabilityId: state.flowTraceabilityId,
    filter: state.params.filter,
  })
);
export const selectExportedFlowDocument = createSelector(featureDocumentTraceability, (state) => state.exportedSuivi);