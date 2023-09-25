import { createFeatureSelector, createSelector } from '@ngrx/store';
import { manageUserKey } from './manage-user.reducer';
import { ProfileAssigned } from '../../models';

const managerUserSelector = createFeatureSelector<any>(manageUserKey);

export const selectAllProfile = createSelector(managerUserSelector, (state) => state.allprofiles as ProfileAssigned[]);

export const selectPageOptions = createSelector(managerUserSelector, (state) => ({
  pageSize: state.pageSize,
  page: state.page,
  length: state.allprofiles.length
}));

export const selectReturnAddressLevel = createSelector(managerUserSelector, (state) => state.selectReturnAddressLevel);
