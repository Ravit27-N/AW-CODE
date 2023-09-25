import { createFeatureSelector, createSelector } from '@ngrx/store';
import { featureFeedFormKey } from './directory-feed-form.reducer';

const featureFeedForm = createFeatureSelector(featureFeedFormKey);

export const selectFeedData = createSelector(featureFeedForm, (state: any) => state.data);

export const selectTableSchemas = createSelector(featureFeedForm, (state: any) => state.schemes);

export const selectSelectedRow = createSelector(featureFeedForm, (state: any) => state.selectedRow)

export const selectPagination = createSelector(featureFeedForm, (state: any) => ({ page: state.page, pageSize: state.pageSize, total: state.total }))
