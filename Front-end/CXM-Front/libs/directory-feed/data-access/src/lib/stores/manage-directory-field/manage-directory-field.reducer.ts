import { createReducer, on } from '@ngrx/store';
import * as fromActions$ from './manage-directory-field.action';
import { DirectoryFeedField } from '../../models';
import { state } from '@angular/animations';

export const manageDirectoryFieldKey = 'manage-directory-field-key';

const initState: DirectoryFeedField = {
  directoryId: 0,
  directoryName: '',
  fields: [],
  shareDirectory: false,
  isLoaded: false,
};

export const ManageDirectoryFieldReducer = createReducer(
  initState,
  on(fromActions$.getDirectoryFieldSuccess, (state, props) => ({
    ...state,
    ...props.fields,
    isLoaded: true,
  })),
  on(fromActions$.destroyDirectoryFields, () => ({ ...initState }))
);
