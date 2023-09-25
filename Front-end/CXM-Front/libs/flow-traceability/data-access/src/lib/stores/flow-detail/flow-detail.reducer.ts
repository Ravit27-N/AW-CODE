import { createReducer, on } from '@ngrx/store';
import * as fromAction from './flow-detail.action';

export const featureFlowDetailKey = 'feature-flow-detail';

const flowDetailInitial = {
  id: 0,
  flowTraceabilityDetail: {},
  flowCampaignDetail: {}
};

export const flowDetailReducer = createReducer(
  flowDetailInitial,
  on(fromAction.loadFlowHistoryDetailSuccess, (state, props) => ({
    ...state,
    flowTraceabilityDetail: props.flowTraceabilityDetail
  })),
  on(fromAction.unloadFlowHistoryDetail, () => ({ ...flowDetailInitial })),
  on(fromAction.flowCampaignDetailSuccess, (state, props) => ({
    ...state,
    flowCampaignDetail: props.flow
  }))
);
