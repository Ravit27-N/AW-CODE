import { createFeatureSelector, createSelector } from "@ngrx/store";
import { featureDashboardGraphKey } from './graph.reducers';
import { IAsyncLoader, IGraphCannelEnvoyResult, IGraphDepositModeResult, IGraphEvolutionResult, IGraphFlowTrackingResult } from '../../models'

const graphFeatureSelector = createFeatureSelector(featureDashboardGraphKey);


export const selectGraphChannel = createSelector<any, any, IAsyncLoader<IGraphCannelEnvoyResult>>(graphFeatureSelector, ((state: any) => state.graphChannel));

export const selectGraphDeposit = createSelector<any, any, IAsyncLoader<IGraphDepositModeResult>>(graphFeatureSelector, ((state: any) => state.graphDeposit));

export const selectGraphFlowtracking = createSelector<any, any, IAsyncLoader<IGraphFlowTrackingResult>>(graphFeatureSelector, ((state: any) => state.graphFlowtracking));

export const selectGraphEvolution = createSelector<any, any, IAsyncLoader<IGraphEvolutionResult>>(graphFeatureSelector, ((state: any) => state.graphEvolution ))

export const selectGraphRefreshTime = createSelector(graphFeatureSelector, (state: any) => state.refreshTime);
export const selectGraphFilter = createSelector(graphFeatureSelector, (state: any) => state.filter);
export const selectGraphAllStates = createSelector(graphFeatureSelector, (state: any) => state);
export const selectGraphDashboardStatesRequestedAt = createSelector(graphFeatureSelector, (state: any) => state.requestedAt);
