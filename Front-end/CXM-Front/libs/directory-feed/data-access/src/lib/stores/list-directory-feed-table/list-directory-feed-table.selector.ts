import { createFeatureSelector, createSelector } from '@ngrx/store';
import { listDirectoryFeedKey } from './list-directory-feed-table.reducer';


const directoryFeedSelector = createFeatureSelector<any>(listDirectoryFeedKey);

export const selectDirectoryFeedList = createSelector(
  directoryFeedSelector,
  (state) => state
);

export const selectSelectedFeedRow = createSelector(
  directoryFeedSelector,
  (state) => state.directoryFeed
)

export const selectValidatedDirectoryFeedingBy = createSelector(
  directoryFeedSelector,
  (state) => state.directoryFeedingBy
);
