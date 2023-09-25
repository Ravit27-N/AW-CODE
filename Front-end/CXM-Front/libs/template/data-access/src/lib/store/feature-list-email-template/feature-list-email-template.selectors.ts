import { createFeatureSelector, createSelector } from '@ngrx/store';
import { featureListEmailTemplateFeatureKey } from './feature-list-template.reducer';

export const getFeatureListEmailTemplateState = createFeatureSelector<any>(
  featureListEmailTemplateFeatureKey
);

// TODO: remove this selector getFeatureListEmailTemplate, should not select all fields
export const getFeatureListEmailTemplate = createSelector(
  getFeatureListEmailTemplateState,
  (response) => response
);

export const selectTemplateModelList = createSelector(
  getFeatureListEmailTemplateState,
  (state) => state.response
);
export const selectTemplateModelListFilter = createSelector(
  getFeatureListEmailTemplateState,
  (state) => state.filters
);

export const getFeatureListEmailTemplateFilter = createSelector(
  getFeatureListEmailTemplateState,
  (state) => state.filters
);
export const selectTotalTemplate = createSelector(
  getFeatureListEmailTemplate,
  (state: any) => state?.response?.total
);
