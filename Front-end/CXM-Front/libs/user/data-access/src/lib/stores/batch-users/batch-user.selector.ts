import { createFeatureSelector, createSelector } from '@ngrx/store';
import { BatchUserStateModel } from '../../models';
import { manageBatchUserKey } from './batch-user.reducer';

const managerBatchUserSelector =
  createFeatureSelector<BatchUserStateModel>(manageBatchUserKey);

export const selectAllBatchUserState = createSelector(
  managerBatchUserSelector,
  (state) => state
);
