import { createAction, props } from '@ngrx/store';
import { Params } from '@cxm-smartflow/shared/data-access/model';
import { ListProfileCriteria, ProfileListModel, ProfileStorageModel } from '../../models';
import { HttpErrorResponse } from '@angular/common/http';

export const loadProfileList = createAction(
  '[Cxm profile / load list of profile]'
);

export const unloadProfilelist = createAction(
  '[Cxm profile / unload list of profile]'
)

export const loadProfileListSuccess = createAction(
  '[Cxm profile / load list of profile success]',
  props<{
    model?: ProfileListModel,
    isLoading?: boolean
  }>()
);

export const loadProfileListFail = createAction(
  '[Cxm profile / load list of profile fail]',
  props<{
    error?: any,
    isLoading?: boolean
  }>()
);

export const loadProfileListDefaultValue: Params = {
  sortByField: 'createdAt',
  sortDirection: 'desc',
  page: 1,
  pageSize: 10
};

export const validateModifyAndDeleteButton = createAction(
  '[Profile list / validate modify and delete button]',
  props<{
    profileId?: number,
    name: string,
    clickAble?: boolean,
    createdBy?: string
  }>()
);

export const loadProfileListFilterChangeAction = createAction(
  '[Profile List / filter change]',
  props<ProfileStorageModel>()
);

export const refreshProfileList = createAction(
  '[Profile list / refresh]',
  props<{
    profileId?: number,
    clickAble?: boolean
  }>()
);

export const showDeleteDialogPopup = createAction(
  '[Delete profile / show dialog pop up]',
  props<{
    profileId: string,
    name: string,
    user?: string[]
  }>()
);

export const attemptToDeleteProfile = createAction(
  '[Delete profile / attemp to delete profile]',
  props<{
    profileId: string,
    name: string
  }>()
);

export const cancelledDeleteProfile = createAction(
  '[Delete profile / cancelled delete profile]',
  props<{
    error?: any
  }>()
);

export const deleteProfile = createAction(
  '[Delete profile / delete by name]',
  props<{
    id: string
  }>()
);

export const deleteProfileSuccess = createAction(
  '[Delete profile / delete success]'
);


export const showDeleteSuccessMessage = createAction(
  '[Delete profile / show success message]'
);

export const deleteProfileFail = createAction(
  '[Delete profile / fail]',
  props<{
    error?: any
  }>()
);

export const redirectToUpdateProfile = createAction(
  '[Update profile / redirect to update profile page]',
  props<{
    id: number,
    clientId?: number
  }>()
);

export const redirectCreateProfile = createAction(
  '[Create profile / redirecto create profile page]'
)

export const clearStateProfile = createAction(
  '[List profile / clear state]'
)

// Get all clients.
export const getAllClient = createAction('[cxm-profile / get all clients ]');
export const getAllClientSuccess = createAction('[cxm-profile / get all client successfully ]', props<{ listClientCriteria: ListProfileCriteria[] }>());
export const getAllClientFail = createAction('[cxm-profile / get all client fail ]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Filter options change.
export const searchTermChange = createAction('[cxm-profile / search term change]', props<{ filter: string }>());
export const filterClientBoxChange = createAction('[cxm-profile / filter client box change]', props<{ clientIds: number[]}>());
export const updateFilterOption = createAction('[cxm-profile / update filter option criteria]', props<{ filter: string, clientIds: number[]}>());
