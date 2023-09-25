import {featureApprovalKey, FilterCriteriaState} from './approval.reducer';
import { createFeatureSelector, createSelector } from '@ngrx/store';

const approvalFeatureSelector = createFeatureSelector<any>(featureApprovalKey);
export const selectApprovalPanel = createSelector(approvalFeatureSelector, (state) => state.open);

export const selecApprovalFlowList = createSelector(approvalFeatureSelector, (state) => state.approveList)
export const selectApprovalFilter = createSelector(approvalFeatureSelector, (state) => state.filters)


export const selectApprovalFilterCriteria = createSelector(approvalFeatureSelector, (state) => state.criteria as FilterCriteriaState);
