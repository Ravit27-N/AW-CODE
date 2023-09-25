import { createFeatureSelector, createSelector } from '@ngrx/store';
import { ManageDirectoryFeedKey } from './manage-directory-feed.reducer';
import { ManageDirectoryFeedStateType } from '../../models';

const ManageDirectoryFeedSelector = createFeatureSelector<ManageDirectoryFeedStateType>(
  ManageDirectoryFeedKey
);

// Only uses in effects.
export const selectAllManageDirectoryStates = createSelector(
  ManageDirectoryFeedSelector,
  (state) => state
);
export const selectDirectoryFeedTables = createSelector(
  ManageDirectoryFeedSelector,
  (state) => state.directoryFeedTables
);

export const selectDirectoryFieldDetail = createSelector(
  ManageDirectoryFeedSelector,
  (state) => state.directoryFeedField
);

export const selectDirectoryFeedDetail = createSelector(
  ManageDirectoryFeedSelector,
  (state) => state.details
);

export const selectDirectoryFieldKeyLabel = createSelector(
  ManageDirectoryFeedSelector,
  (state) => state.labelFieldKey
);

export const selectErrorField = createSelector(
  ManageDirectoryFeedSelector,
  (state) => state.fieldError
);

export const selectDirectoryFeedIsLocked = createSelector(
  ManageDirectoryFeedSelector,
  (state) => state.isLocked
);
