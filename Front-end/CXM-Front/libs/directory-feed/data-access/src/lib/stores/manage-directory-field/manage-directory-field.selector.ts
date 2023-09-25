import { createFeatureSelector, createSelector } from '@ngrx/store';
import { manageDirectoryFieldKey } from './manage-directory-field.reducer';
import { DirectoryFeedField } from '../../models';

const ManageDirectoryFieldSelector = createFeatureSelector<DirectoryFeedField>(
  manageDirectoryFieldKey
);

export const selectDirectoryFields = createSelector(
  ManageDirectoryFieldSelector,
  (state) => state
);

export const selectIsSharedDirectory = createSelector(
  ManageDirectoryFieldSelector,
  (state) => ({isSharedDirectory: state.shareDirectory, isLoaded: state.isLoaded})
);
