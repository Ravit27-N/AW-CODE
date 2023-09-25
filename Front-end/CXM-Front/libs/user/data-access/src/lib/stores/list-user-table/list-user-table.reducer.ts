import * as fromUserAction from './list-user-table.action';
import { createReducer, on } from '@ngrx/store';
import { Params } from '@cxm-smartflow/shared/data-access/model';

export const listUserKey = 'list-of-user-key';

const listUserInitState: any = {
  userListResponse: {},
  isLoading: false,
  refreshDate: new Date(),
  modificationUserList: [],

  selectionOpen: false,
  filters: {
    page: 1, pageSize: 10, sortByField: 'email', sortDirection: 'asc', filter: ''
  },
  orgProfiles: [],
  orgServices: [],
  orgDivisions: [],
  clientCriteria: [],
  clients: [],
  clientWrappers: [],
  divServiceWrappers: []
};

export const defaultPagination: Params = {
  page: 1,
  pageSize: 15,
  sortByField: 'createdAt',
  sortDirection: 'desc'
};

export const listUserReducer = createReducer(
  listUserInitState,
  on(fromUserAction.loadUserListSuccess, (state, props) => ({
    ...state,
    userListResponse: props.response,
    isLoading: false,
    refreshDate: new Date(),
    modificationUserList: []
  })),
  on(fromUserAction.loadUserListFail, () => ({
    ...listUserInitState,
    isLoading: false
  })),
  on(fromUserAction.loadModificationUserList, (state, props) => ({
    listUserInitState,
    ...state,
    ...props,
    isLoading: false,
    modificationUserList: props.modificationUserList
  })),
  on(fromUserAction.refreshUserList, (state, props) => ({
    ...state,
    ...props,
    isLoading: false,
    refreshDate: new Date()
  })),
  on(fromUserAction.entriesBatchOfModificationUser, (state, props) => ({
    ...state,
    modificationBatchUserId: props.modificationBatchUserId
  })),
  on(fromUserAction.mapBatchOfModificationUser, (state, props) => ({
    ...state,
    filteredModifiedUser: props.filteredModifiedUser
  })),
  on(fromUserAction.loadClientServiceSuccess, (state, props) => ({
    ...state,
    clients: props.clients,
    clientWrappers: props.clientWrappers,
    divServiceWrappers: props.divServiceWrappers
  })),
  on(fromUserAction.unloadUserForm, (state, props) => ({
    ...state,
    userDetails: undefined,
    clients: [],
    clientWrappers: [],
    divServiceWrappers: []
  })),
  on(fromUserAction.loadClientCriteriaSuccess, (state, props) => ({
    clientCriteria: props.clientCriteria
  })),
  on(fromUserAction.loadClientCriteriaFail, (state, props) => ({
    clientCriteria: []
  })),
  on(fromUserAction.loadUserList, (state, props) => {
    return { ...state, filters: props.params }
  }),

  on(fromUserAction.setSelectionPanel, (state, props) => ({ ...state, selectionOpen: props.active })),
  on(fromUserAction.openSelectionPanel, (state) => ({ ...state, selectionOpen: true })),
  on(fromUserAction.closeSelectionPanel, (state) => ({ ...state, selectionOpen: false })),

  on(fromUserAction.setOrganizationProfile, (state, props) => ({ ...state, orgProfiles:  props.profiles })),
  //add new
  on(fromUserAction.setOrganizationDivision, (state, props) => ({ ...state, orgDivisions:  props.divisions })),
  on(fromUserAction.setOrganizationService, (state, props) => ({ ...state, orgServices:  props.services })),
  on(fromUserAction.unloadUserlist, () => ({ ...listUserInitState }))
);
