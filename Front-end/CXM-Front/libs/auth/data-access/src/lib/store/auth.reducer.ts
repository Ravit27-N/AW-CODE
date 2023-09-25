import { createReducer, on } from '@ngrx/store';
import * as fromAction from './auth.actions';

export const AUTH_FEATURE_KEY = 'auth';

const initialState: any ={
  username: '',
  password: '',
  userProfiles: undefined,
  userCredential: {
    usernameValid: true,
    passwordValid: true
  }
}

export const authReducer = createReducer(
  initialState,
  on(fromAction.loadAuthLogin, (state) => ({
    ...state
  })),
  on(fromAction.loadAuthSuccess, (state, {keycloakResponse}) => ({
    ...state,
    keycloakResponse: keycloakResponse,
    showSpinner: true
  })),
  on(fromAction.loadAuthFailure, (state, props) => ({
    ...initialState,
    showSpinner: false,
    error: props?.error
  })),
  on(fromAction.logout, () => ({
    ...initialState
  })),
  on(fromAction.loadUserProfileFail, (state) => ({
    ...state,
    showSpinner: false
  })),
  on(fromAction.loadUserProfileSuccess, (state, props) => ({
    ...state,
    userProfiles: props,
    showSpinner: false
  })),
  on(fromAction.validateUserCredentialSuccess, (state, props) => ({
    ...state,
    userCredential: props?.userCredential
  })),
  on(fromAction.validateUserCredentialFail, (state) => ({
    ...state,
    userCredential: {
      usernameValid: true,
      passwordValid: true
    }
  })),
  on(fromAction.loadUserForceToChangePassword, (state, props) => ({
    ...state,
    userProfiles: props,
    showSpinner: false
  }))
);
