import { createFeatureSelector, createSelector } from '@ngrx/store';
import { featuredFlowDocumentDetailKey } from './flow-document-detail.reducer';

const FlowDocumentDetailSelector = createFeatureSelector(featuredFlowDocumentDetailKey);

// Select flow document detail.
export const selectFlowDocumentDetail = createSelector(FlowDocumentDetailSelector, (state: any) => state?.documentDetail);

// Select list of associate document.
export const selectAssociateDocument = createSelector(FlowDocumentDetailSelector, (state: any) => state?.associateDocument);
export const selectEventHistoryInfo = createSelector(FlowDocumentDetailSelector, (state: any) => state?.eventHistoryInfo);
