import { createFeatureSelector, createSelector } from '@ngrx/store';
import { listUserKey } from './list-user-table.reducer';
import { InputSelectionCriteria } from '@cxm-smartflow/shared/ui/form-input-selection';
import { Client, ClientCriteria, UserList } from '../../models';

const userListSelector = createFeatureSelector<any>(listUserKey);

export const selectListOfUser = createSelector(userListSelector, (state) => ({ userList: state?.contents?.contents }));
export const selectFilteredModifiedUser = createSelector(userListSelector, (state) => ({ filteredModifiedUser: state.filteredModifiedUser }));
export const selectUserDetails = createSelector(userListSelector, (state) => state?.userDetails);
export const selectUserDetailsServiceId = createSelector(userListSelector, (state) => (state.userDetails?.service?.id));
export const selectSelectionOpened = createSelector(userListSelector, (state) => state.selectionOpen);
export const selectUserListFilters = createSelector(userListSelector, (state) => state.filters);
export const selectIsSearchBoxHasFilter = createSelector(userListSelector, (state) => state.filters.filter?.trim()?.length > 0);
export const selectIsSearchBoxHasError = createSelector(userListSelector, (state) => state.filters.filter?.trim()?.length > 0 && state.userListResponse?.total == 0);
export const selectUsersList = createSelector(userListSelector, (state) => state.userListResponse as UserList);
export const selectOrganizationProfiles = createSelector(userListSelector, (state) => state.orgProfiles);

export const selectClientCriteria = createSelector(userListSelector, (state) => state.clientCriteria as ClientCriteria[]);

export const selectClients = createSelector(userListSelector, (state) => state.clients as Client[]);
export const selectClientWrappers = createSelector(userListSelector, (state) => state.clientWrappers as InputSelectionCriteria[]);
export const selectDivServiceWrappers = createSelector(userListSelector, (state) => state.divServiceWrappers as InputSelectionCriteria[]);

//add new
export const selectOrganizationDivisions = createSelector(userListSelector, (state) => state.orgDivisions);//orgDivisions
export const selectOrganizationServices = createSelector(userListSelector, (state) => state.orgServices);//orgServices

// export users 
export const selectExportedUsers = createSelector(userListSelector, (state) => state.exportedUsers);

export const selectDivServiceUserWrappers = createSelector(userListSelector, (state) => state.divServiceWrappers as InputSelectionCriteria[]);