import { featureFlowTraceabilityListKey } from './flow-traceability-list.reducer';
import { createFeatureSelector, createSelector } from '@ngrx/store';
import { FlowFilterCriteriaParams } from '../../models';

export const getFeatureFlowTraceabilityListState = createFeatureSelector<any>(
  featureFlowTraceabilityListKey
);

export const getFeatureFlowTraceabilityList = createSelector(
  getFeatureFlowTraceabilityListState,
  (state) => state
);

export const selectFlowTraceabilityState = createSelector(
  getFeatureFlowTraceabilityListState,
  (state) => state.flowTraceability
);

export const selectShowSearchBoxTooltip = createSelector(
  getFeatureFlowTraceabilityListState,
  (state) => state.showSearchBoxTooltip
);

export const selectFilterCriteriaNotFound = createSelector(
  getFeatureFlowTraceabilityListState,
  (state) => state.isFilterCriteriaNotFound
);

export const selectFlowParams = createSelector(
  getFeatureFlowTraceabilityListState,
  (state: any) => state.params
);

export const selectPaginationWithFilterCriteria = createSelector(
  getFeatureFlowTraceabilityList,
  (state: any) => {
    return {
      page: state?.flowTraceability?.page,
      pageSize: state?.flowTraceability?.pageSize,
      params: state?.params as FlowFilterCriteriaParams
    }
  }
)
