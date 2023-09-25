import { createFeatureSelector, createSelector } from '@ngrx/store';
import {AUTH_FEATURE_KEY} from './auth.reducer';
import { UserCredentialModel } from '../models';

export const getAuthState = createFeatureSelector<any>(
  AUTH_FEATURE_KEY
);

export const getAuth = createSelector(getAuthState, (response) => response);

export const getUserProfiles = createSelector(getAuthState, (response) => response?.userProfiles);

export const selectUserCredential = createSelector(getAuthState, (response) => response?.userCredential as UserCredentialModel);
