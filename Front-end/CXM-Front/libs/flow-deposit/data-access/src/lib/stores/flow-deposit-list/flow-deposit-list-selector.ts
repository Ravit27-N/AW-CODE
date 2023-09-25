import { createFeatureSelector, createSelector } from '@ngrx/store';
import { flowDepositListKey } from './flow-deposit-list-reducer';

const flowDepositListSelector = createFeatureSelector(flowDepositListKey);

export const selectFlowDepositListState = createSelector(flowDepositListSelector, (state: any) => state);

export const selectListLoading = createSelector(flowDepositListSelector, (state: any) => state?.loading as boolean);

export const selectShowSearchBoxTooltip = createSelector(flowDepositListSelector, (state: any) => state?.showSearchBoxTooltip);

export const selectFilterCriterial = createSelector(flowDepositListSelector, (state: any) => state?.filterCriteria);

export const selectUsers = createSelector(flowDepositListSelector, (state: any) => state?.users);
