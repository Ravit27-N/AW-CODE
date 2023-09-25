import { createFeatureSelector, createSelector } from '@ngrx/store';
import { deleteEmailTemplateKey } from './delete-email-template.reducer';

export const getDeleteEmailTemplateState = createFeatureSelector<any>(
  deleteEmailTemplateKey
);

export const getDeleteEmailTemplate = createSelector(
  getDeleteEmailTemplateState,
  (response) => {
    return response;
  }
);
