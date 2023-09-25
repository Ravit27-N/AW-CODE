import { createAction, props } from '@ngrx/store';
import { keyCloakModel, LoginModel } from '@cxm-smartflow/shared/data-access/model';
import { UserCredentialModel } from '../models';

export const init = createAction('[Auth Page] Init');

export const loadAuthLogin = createAction(
  '[Auth] Load Auth Login',
  props<{
    loginForm: LoginModel
  }>()
);

export const loadAuthSuccess = createAction(
  '[Auth] Load Auth Success',
  props<{ keycloakResponse: keyCloakModel }>()
);

export const showLoginSuccess = createAction(
  '[Auth / show success message]'
);

export const loadAuthFailure = createAction(
  '[Auth] Load Auth Failure',
  props<{ error: any, loginForm: LoginModel }>()
);

export const logout = createAction('[Auth] Logout');

export const loadUserProfile = createAction(
  '[Profile / load user profile]',
  props<{
    name: string,
    forceToChangePassword: boolean
  }>()
);

export const loadUserProfileSuccess = createAction(
  '[Profile / load user profile success]',
  props<any>()
);

export const loadUserProfileFail = createAction(
  '[Profile / load user profile fail]',
  props<{ error?: any }>()
);

export const loadUserForceToChangePassword = createAction(
  '[Profile / load user forced to change password]',
  props<any>()
);

export const validateUserCredential = createAction('[Profile] / validate user credential', props<{ loginForm: LoginModel }>());
export const validateUserCredentialSuccess = createAction('[Profile] / validate user credential success', props<{ userCredential: UserCredentialModel }>());
export const validateUserCredentialFail = createAction('[Profile] / validate user credential fail', props<{ error?: any }>());
export const checkNotBlockedAccount = createAction('[Profile] / check not blocked account', props<{ loginForm: LoginModel }>());