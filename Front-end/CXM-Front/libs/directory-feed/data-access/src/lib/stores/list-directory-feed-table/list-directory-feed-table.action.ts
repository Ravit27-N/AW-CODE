import { createAction, props } from '@ngrx/store';
import { Params } from '@cxm-smartflow/shared/data-access/model';
import { DirectoryFeedListResponse, DirectoryFeedModel } from '../../models';

export const loadDirectoryFeedList = createAction(
  '[Cxm directory / load list of directory feed]',
  props<{
    params?: Params;
  }>()
);

export const loadDirectoryFeedListSuccess = createAction(
  '[Cxm directory / load list of directory feed success]',
  props<{
    directoryFeedList?: DirectoryFeedListResponse;
  }>()
);

export const loadDirectoryFeedListFail = createAction(
  '[Cxm directory / load list of directory feed fail]',
  props<{
    errors: any
  }>()
);

export const unloadDirectoryFeedList = createAction(
  '[Cxm directory / unload list of directory feed]',
  props<{
    directoryFeedList?: DirectoryFeedListResponse;
    clickable?: boolean;
  }>()
);

export const refreshDirectoryFeedList = createAction(
  '[Cxm directory / refresh list of directory feed]',
  props<{
    directoryFeed?: DirectoryFeedModel;
    clickable?: boolean;
    params?: Params;
    directoryFeedingBy?: string;
  }>()
);

export const validateModifiedAndDelete = createAction(
  '[Cxm directory / validate modified and delete directory feed button]',
  props<{
    directoryFeed: DirectoryFeedModel;
    clickable: boolean;
  }>()
);

export const navigateToFeed = createAction('[Cxm directory / navigate to feed]', props<{ row: any }>());

export const validateDirectoryFeedingBy = createAction(
  '[Cxm directory / validate directory feeding by]',
  props<{
    directoryFeedingBy?: string,
    directoryCreatedBy?: string
  }>()
);

export const resetValidatedDirectoryFeedingBy = createAction(
  '[Cxm directory / reset validated directory feeding by]',
  props<{
    directoryFeedingBy?: string;
    directoryCreatedBy?: string;
  }>()
);
