import { createFeatureSelector, createSelector } from '@ngrx/store';
import { listProfileKey } from './list-profile-table.reducer';

const profileListSelector = createFeatureSelector<any>(listProfileKey);

export const selectProfileList = createSelector(profileListSelector, (state) => state);
export const selectFitler = createSelector(profileListSelector, ({ sortDirection, sortByField, page, pageSize, filter, clientIds }) =>
({ sortDirection, sortByField, page, pageSize, filter, clientIds }) );

export const selectClientCriteria = createSelector(profileListSelector, (state) => state.listClientCriteria);
export const selectIsFilterError = createSelector(profileListSelector, (state) => state.isFilterError);
export const selectHasOptionFilter = createSelector(profileListSelector, (state) => state.hasOptionFilter);
