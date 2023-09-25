import * as fromListDirectoryFeedAction from './list-directory-feed-table.action';
import { createReducer, on } from '@ngrx/store';
import { Params } from '@cxm-smartflow/shared/data-access/model';

export const listDirectoryFeedKey = 'list-of-directory-feed-key';

const listDirectoryFeedInitState: any = {
  refreshDate: new Date(),
  params: {
    page: 1,
    pageSize: 15,
    sortDirection: 'desc',
    sortByField: 'createdAt'
  }
};

export const defaultDirectoryFeedPagination: Params = {
  page: 1,
  pageSize: 15,
  sortDirection: 'desc',
  sortByField: 'createdAt'
};

export const listDirectoryFeedReducer = createReducer(
  listDirectoryFeedInitState,
  on(fromListDirectoryFeedAction.loadDirectoryFeedList, (state, props) => ({
    ...state,
    ...props,
    refreshDate: new Date()
  })),
  on(fromListDirectoryFeedAction.loadDirectoryFeedListSuccess, (state, props) => ({
    ...state,
    ...props,
    refreshDate: new Date()
  })),
  on(fromListDirectoryFeedAction.loadDirectoryFeedListFail, () => ({
    ...listDirectoryFeedInitState
  })),
  on(fromListDirectoryFeedAction.unloadDirectoryFeedList, () => ({
    ...listDirectoryFeedInitState,
    directoryFeedList: {},
    clickable: false
  })),
  on(fromListDirectoryFeedAction.validateModifiedAndDelete, (state, props) => ({
    ...state,
    ...props,
    refreshDate: new Date()
  })),
  on(fromListDirectoryFeedAction.refreshDirectoryFeedList, (state, props) => ({
    ...state,
    directoryFeedList: {},
    clickable: false,
    directoryFeedingBy: ''
  })),
  on(fromListDirectoryFeedAction.validateDirectoryFeedingBy, (state, props) => ({
    ...state,
    ...props,
  })),
  on(fromListDirectoryFeedAction.resetValidatedDirectoryFeedingBy, (state, props) => ({
    ...state,
    directoryFeedingBy: '',
    directoryCreatedBy: ''
  }))
);
