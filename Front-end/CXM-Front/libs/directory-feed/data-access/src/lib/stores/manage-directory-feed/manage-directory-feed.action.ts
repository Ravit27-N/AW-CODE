import {createAction, props} from '@ngrx/store';
import {HttpErrorResponse} from '@angular/common/http';
import {
  DirectoryFeedField,
  DirectoryFeedForm,
  DirectoryFeedListResponse,
  FieldDetail,
  ListDirectoryFeedValue
} from '../../models';


// Get directory feed list.
export const getDirectoryFeedList = createAction(
  '[directory feed / get directory feed list]',
  props<{
    page: number;
    pageSize: number;
    sortByField: string;
    sortDirection: string;
  }>()
);

export const getDirectoryFeedListSuccess = createAction(
  '[directory feed / get directory feed list success]',
  props<{ directoryFeedList: DirectoryFeedListResponse }>()
);

export const getDirectoryFeedListFails = createAction(
  '[directory feed / get directory feed list fails]',
  props<{
    httpErrorResponse: HttpErrorResponse;
  }>()
);

// Get directory feed details.
export const getDirectoryFeedDetail = createAction(
  '[directory feed / get directory feed detail]',
  props<{ id: number }>()
);
export const getDirectoryFeedDetailSuccess = createAction(
  '[directory feed / get directory feed detail success]',
  props<{
    directoryFeedDetail: DirectoryFeedField;
  }>()
);
export const getDirectoryFeedDetailFail = createAction(
  '[directory feed / get directory feed detail fail]',
  props<{
    httpErrorResponse: HttpErrorResponse;
  }>()
);

export const unloadManageDirectoryState = createAction(
  '[directory feed / unload manage directory state]'
);

// Import CSV file.
export const importDirectoryFeedCsvFileInSuccess = createAction(
  '[directory feed / import directory feed csv file success]',
  props<{
    directoryId: number;
    page: number;
    pageSize: number;
    ignoreHeader: boolean;
    removeDuplicated: boolean;
  }>()
);

export const submitImportDirectoryFeed = createAction(
  '[directory feed / submit imported directory feed]',
  props<{ fileId: string }>()
);

export const submitImportDirectoryFeedSuccess = createAction(
  '[directory feed / submit imported directory feed success]'
);

export const submitImportDirectoryFeedFail = createAction(
  '[directory feed / submit imported directory feed fail]',
  props<{
    httpErrorResponse: HttpErrorResponse;
  }>()
);

export const loadDirectoryFeedDetails = createAction(
  '[directory feed - details / load directory feed details]',
  props<{
    directoryId: number;
    page?: number;
    pageSize?: number;
    sortByField?: string;
    sortDirection?: string;
    filter?: string;
  }>()
);

export const loadDirectoryFeedDetailsSuccess = createAction(
  '[directory feed - details / load directory feed details success]',
  props<ListDirectoryFeedValue>()
);

export const loadDirectoryFeedDetailsFail = createAction(
  '[directory feed - details / load directory feed details fail]');


export const submitDirectoryFeed = createAction('[DIRECTORY FEED DETAIL / submit directory feed]', props<{ dataDirectoryFeeds: FieldDetail[] }>());
export const submitDirectoryFeedSuccess = createAction('[DIRECTORY FEED DETAIL /  submit directory feed success]');
export const submitDirectoryFeedFail = createAction('[DIRECTORY FEED DETAIL / submit directory feed fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

export const submitDirectoryFeedValue = createAction(
  '[directory feed - modified / submit directory feed value]',
  props<{
    directoryId: number;
    data: DirectoryFeedForm;
  }>()
);

export const submitDirectoryFeedValueSuccess = createAction(
  '[directory feed - modified / submit directory feed value success]',
  props<{
    directoryId: number;
    typeMode: 'modify' | 'delete'
  }>()
);

export const submitDirectoryFeedValueFail = createAction(
  '[directory feed - modified / submit directory feed value fail]',
  props<{
    httpErrorResponse: HttpErrorResponse;
      typeMode: 'modify' | 'delete';
  }>()
);

export const keepDirectoryFieldKeyLabel = createAction(
  '[directory feed - import / keep directory field key label]',
  props<{
    label: string;
  }>()
);

export const keepDirectoryFeedLocked = createAction(
    '[directory feed - locked / keep directory feed locked]',
    props<{
        isLocked: boolean;
    }>()
);
