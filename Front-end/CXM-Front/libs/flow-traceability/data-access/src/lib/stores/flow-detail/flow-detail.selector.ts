import { createFeatureSelector, createSelector } from '@ngrx/store';
import { featureFlowDetailKey } from './flow-detail.reducer';

const FlowDetailSelector = createFeatureSelector(featureFlowDetailKey);

// Flow traceability detail.
export const flowTraceabilityDetailSelector = createSelector(FlowDetailSelector, (state: any) => state?.flowTraceabilityDetail);

// Flow traceability campaign detail.
export const flowTraceabilityCampaignDetailSelector = createSelector(FlowDetailSelector, (state: any) => state?.flowCampaignDetail);
