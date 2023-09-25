import { createAction, props } from '@ngrx/store';
import { CreateUserRequestModel, UpdateUserRequestModel } from '../../models';
import { HttpErrorResponse } from '@angular/common/http';

export const submitUserForm = createAction(
  '[Cxm user / submit user info form]',
  props<{
    email?: string,
    fullName?: string,
    serviceId?: number,
    password?: string,
    confirmedPassword?: string,
    isSubmit?: boolean,
    profiles?: number[]
  }>()
);

export const unloadFormUser = createAction('[cxm user / unload user from]');

export const loadProfileList = createAction('[cxm user / load profile list]', props<{ page: number, pageSize: number }>());
export const loadProfileListSuccess = createAction('[cxm user / load profile list success]', props<{ allprofiles: [], page: number, pageSize: number }>());

export const lazyLoadPaging = createAction('[cxm user / load profile lazy]');

export const entryUserForm = createAction('[cxm user / user entry data]', props<{ isEntering?: boolean }>());

export const submitCreateUserFrom = createAction('[cxm user / submit create user form]', props<{ createUserModel: CreateUserRequestModel }>());
export const submitCreateUserSuccess = createAction('[cxm user / submit create user form success]');
export const submitCreateUserFails = createAction('[cxm user / submit create user form fails]', props<{ httpErrorResponse: HttpErrorResponse }>());

export const navigateToEditBatchUserForm = createAction('[cxm user / navigate to batch user form]', props<{ editBatchUsers: string[] }>());

export const submitModifyBatchOfUser = createAction('[cxm user / submit batch of user]', props<{ userIds: string[], profiles: number[] }>());
export const submitModifyBatchOfUserSuccess = createAction('[cxm user / submit batch of user success]');
export const submitModifyBatchOfUserFails = createAction('[cxm user / submit batch of user fails', props<{ httpErrorResponse: HttpErrorResponse }>());

export const submitModifySingleUser = createAction('[cxm user / submit single user]', props<{ updateUserModel: UpdateUserRequestModel }>());
export const submitModifySingleUserSuccess = createAction('[cxm user / submit single user success]');
export const submitModifySingleUserFails = createAction('[cxm user / submit single user fails]', props<{ httpErrorResponse: HttpErrorResponse }>());

export const clearProfiles = createAction('[cxm user] / clear all profile');
export const getAllProfileByServiceId = createAction('[cxm user] / get all profile by service id', props<{ serviceId: number }>());
export const getAllProfileByServiceIdSuccess = createAction('[cxm user] / get all profile by service id success', props<{ profiles: any[] }>());
export const getAllProfileByServiceIdFail = createAction('[cxm user] / get all profile by service id fail', props<{ httpErrorResponse: HttpErrorResponse }>());

export const switchReturnAddressLevel = createAction('[cxm user] / get all profile by service id fail', props<{ returnAddressLevel: string | number }>());
