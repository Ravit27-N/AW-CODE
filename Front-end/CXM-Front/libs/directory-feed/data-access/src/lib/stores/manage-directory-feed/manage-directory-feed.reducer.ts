import { createReducer, on } from '@ngrx/store';
import * as fromActions$ from './manage-directory-feed.action';
import { ManageDirectoryFeedStateType } from '../../models';

export const ManageDirectoryFeedKey = 'manage-directory-feed-key';

const ManageDirectoryFeedState: ManageDirectoryFeedStateType = {
  // Definition directory details.
  definitionDirectoryDetail: {
    directoryId: 0,
    directoryName: '',
    fields: [],
    shareDirectory: false
  },
  directoryFeedTables: {
    contents: [],
    page: 1,
    pageSize: 10,
    total: 0,
  },
  // Import csv file.
  importDirectoryFeedCsvListCriteria: {
    directoryId: 0,
    page: 1,
    pageSize: 10,
    filter: '',
    ignoreHeader: false,
    removeDuplicated: false,
  },
  directoryFeedField: {
    directoryId: 0,
    directoryName: '',
    fields: [],
    shareDirectory: false
  },
  details: {
    page: 1,
    pageSize: 10,
    contents: [],
    total: 0,
    isLoad: false,
  },
  dataDirectoryFeeds: [],
  labelFieldKey: '',
  errorCode: 0,
  fieldError: '',
  isLocked: false,
  isSubmitError: false,
};

export const ManageDirectoryFeedReducer = createReducer(
  ManageDirectoryFeedState,
  on(fromActions$.getDirectoryFeedListSuccess, (state, props) => {
    return { ...state, directoryFeedTables: props.directoryFeedList };
  }),
  on(fromActions$.unloadManageDirectoryState, (state) => {
    return ManageDirectoryFeedState;
  }),
  on(fromActions$.importDirectoryFeedCsvFileInSuccess, (state, props) => {
    return { ...state, importDirectoryFeedCsvListCriteria: { ...props } };
  }),
  on(fromActions$.getDirectoryFeedDetailSuccess, (state, props) => {
    return { ...state, directoryFeedField: props.directoryFeedDetail };
  }),
  on(fromActions$.loadDirectoryFeedDetailsSuccess, (state, props) => ({
    ...state,
    details: { ...props, isLoad: true },
  })),
  on(fromActions$.submitDirectoryFeed, (state, props) => {
    return {
      ...state,
      dataDirectoryFeeds: props.dataDirectoryFeeds,
      fieldError: '',
    };
  }),
  on(fromActions$.keepDirectoryFieldKeyLabel, (state, props) => ({
    ...state,
    labelFieldKey: props.label,
  })),
  on(fromActions$.submitDirectoryFeedFail, (state, props) => {
    const debugMessage = JSON.parse(
      props.httpErrorResponse.error.apierrorhandler.debugMessage
    );
    return {
      ...state,
      errorCode: props.httpErrorResponse.error.apierrorhandler.statusCode,
      fieldError: debugMessage.directory_key,
    };
  }),
  on(fromActions$.keepDirectoryFeedLocked, (state, props) => ({
    ...state,
    isLocked: props.isLocked,
  })),
  on(fromActions$.submitDirectoryFeedValueFail, (state) => ({
    ...state,
    isSubmitError: true,
  })),
  on(fromActions$.submitDirectoryFeedValueSuccess, (state) => ({
    ...state,
    isSubmitError: false,
  }))
);
