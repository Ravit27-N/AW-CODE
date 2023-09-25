import * as fromProfileAction from './list-profile-table.action';
import { createReducer, on } from '@ngrx/store';
import { updateFilterOption } from './list-profile-table.action';

export const listProfileKey = 'list-of-profile-key';

const listProfileInitState: any = {
  contents: [],
  isLoading: false,
  sortDirection: 'desc',
  sortByField: 'lastModified',
  page: 1,
  pageSize: 10,
  total: 0,
  filter: '',
  clientIds: [],
  isFilterError: false
}

export const listProfileReducer = createReducer(
  listProfileInitState,
  on(fromProfileAction.loadProfileListSuccess, (state, { isLoading, model }) => {
    const isFilterError = state?.filter?.length > 0 && model?.total === 0;
    return {...state, ...model, isLoading: isLoading, refreshDate: new Date(), isFilterError};
  }),
  on(fromProfileAction.loadProfileListFail, (state) => ({...state, isLoading: false})),
  on(fromProfileAction.validateModifyAndDeleteButton, (state, props) => ({...state, ...props})),
  on(fromProfileAction.loadProfileListFilterChangeAction, (state, props) => {
    return { ...state, ...props }
  }),
  on(fromProfileAction.refreshProfileList, (state) => ({ ...state, page: 1, pageSize: 10  })), // reset pagination to default when refresh
  on(fromProfileAction.unloadProfilelist, (state) => ({...state, clickAble: false, name: undefined, profileId: undefined })),
  on(fromProfileAction.clearStateProfile, () => ({...listProfileInitState })),
  on(fromProfileAction.getAllClientSuccess, (state, props) => ({...state, listClientCriteria: props.listClientCriteria })),
  on(fromProfileAction.searchTermChange, (state, props) => {
    const hasOptionFilter = (state.clientIds.length > 0) || props.filter.length > 0;
    return {...state, filter: props.filter, hasOptionFilter, page: 1, pageSize: 10 };
  }),
  on(fromProfileAction.filterClientBoxChange, (state, props) => {
    const hasOptionFilter = (state.filter.length > 0) || props.clientIds.length > 0;
    return {...state, clientIds: props.clientIds, hasOptionFilter, page: 1, pageSize: 10 };
  }),
  on(fromProfileAction.updateFilterOption, (state, props) => {
    const hasOptionFilter = (props.filter.length > 0) || props.clientIds.length > 0;
    return {...state, clientIds: props.clientIds, filter: props.filter, hasOptionFilter }
  })
);
