import { featureApprovalDocKey } from './document-approval.reducer';
import { createFeatureSelector, createSelector } from '@ngrx/store';

const featureApprovalDoc = createFeatureSelector(featureApprovalDocKey);



export const selectApprovalDoclist = createSelector(featureApprovalDoc, (state: any) => state.approveDocList);
export const selectApprovalDocPanel =  createSelector(featureApprovalDoc, (state: any) => state.open);
export const selectApprovalDocFilter = createSelector(featureApprovalDoc, (state: any) => state.filters);
