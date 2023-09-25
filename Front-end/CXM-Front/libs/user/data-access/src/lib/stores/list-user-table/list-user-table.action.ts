import { createAction, props } from '@ngrx/store';
import {
  Client,
  ClientCriteria,
  ISingleEditedUser,
  ServiceAssigned,
  UserDetail, UserList, exportUsersCsv,
} from '../../models';
import { Params } from '@cxm-smartflow/shared/data-access/model';
import { HttpErrorResponse } from '@angular/common/http';
import { InputSelectionCriteria } from '@cxm-smartflow/shared/ui/form-input-selection';
import { BehaviorSubject } from 'rxjs';

export const loadUserList = createAction('[Cxm user / load list of user]', props<{ params?: Params }>());
export const loadUserListSuccess = createAction('[Cxm user / load list of user success]', props<{ response?: UserList, isLoading?: boolean }>());
export const loadUserListFail = createAction('[Cxm user / load list of user fail]', props<{ error?: any, isLoading?: boolean }>());
export const unloadUserlist = createAction('[cxm user / unload user list]');

export const loadModificationUserList = createAction('[Cxm user / select modification user list]', props<{ modificationUserList?: string[], isLoading?: boolean }>());

export const refreshUserList = createAction('[Cxm user / refresh user list]', props<{ isRefresh?: boolean }>());

export const deleteUser = createAction('[Cxm user / delete batch of users]', props<{ userIds: string[] }>());
export const deleteUserSuccess = createAction('[Cxm user / delete batch of users success]', props<{ userIds: string[] }>());
export const deleteUserFail = createAction('[Cxm user / delete batch of users false]', props<{ httpErrorResponse: HttpErrorResponse }>());

export const entriesBatchOfModificationUser = createAction('[cxm user / entries batch of modification user]', props<{ modificationBatchUserId: string [], userList: ISingleEditedUser[] }>());
export const mapBatchOfModificationUser = createAction('[cxm user] / map batch of modification user', props<{ filteredModifiedUser: string[] }>());

export const navigateToUpdateSingleUser = createAction('[cxm user / navigate to update sing user]', props<{ updatedUserId: string }>());

export const loadClientCriteria = createAction('[cxm user] / load client criteria', props<{sortDirection: string}>());
export const loadClientCriteriaSuccess = createAction('[cxm user] / load client criteria success', props<{clientCriteria: ClientCriteria[]}>());
export const loadClientCriteriaFail = createAction('[cxm user] / load client criteria fail', props<{error: HttpErrorResponse}>());

export const loadClientService = createAction('[cxm user] / load client service', props<{clientId?: number}>());
export const loadClientServiceSuccess = createAction('[cxm user] / load client service success',
  props<{ clients: Client[], clientWrappers?: InputSelectionCriteria[], divServiceWrappers?: InputSelectionCriteria[] }>());
export const loadClientServiceFail = createAction('[cxm user] / load client service fail', props<{ error: HttpErrorResponse }>());

export const unloadUserForm = createAction('[cxm user / get list of user fails]', props<{ userDetails?: UserDetail, services?: ServiceAssigned[] }>());


export const openSelectionPanel = createAction('[cxm user / open user selection]');
export const closeSelectionPanel = createAction('[cxm user / close user selection]');
export const setSelectionPanel = createAction('[cxm user / set user selection]', props<{ active: boolean }>());

export const loadOrganizationProfile = createAction('[cxm user / load org profiles]', props<{clientIds?: number}>());//updated
export const setOrganizationProfile = createAction('[cxm user / set org profiles]', props<{ profiles: { id: number, name: string }[] }>());
//add new
export const loadClientDivision = createAction('[cxm user] / load client division', props<{clientIds?: any}>());
export const setOrganizationDivision = createAction('[cxm user / set org divisions]', props<{ divisions: { id: number, name: string }[] }>());

export const loadServices = createAction('[cxm user] / load client service', props<{clientIds?: any,divisionIds?: any}>());
export const setOrganizationService = createAction('[cxm user / set org service]', props<{ services: { id: number, name: string }[] }>());
//export users
export const exportUsers = createAction('[cxm user / export users]', props<{ services: { data: exportUsersCsv }[], submittedCsvExport$: BehaviorSubject<boolean> }>());
export const exportUsersSuccess = createAction('[cxm user / export users success] Export Users Success',props<{ response: any }>());

export const exportUsersFailure = createAction('[cxm user / export users fail] Export Users Failure',props<{ error: any }>());

export const loadClientServiceInUser = createAction('[cxm user] / load client service in user', props<{userId?: number}>());