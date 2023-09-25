import { createFeatureSelector, createSelector } from '@ngrx/store';
import { featureManageProfileFormKey } from './manage-profile.reduce';


const featureMangeProfileForm = createFeatureSelector(featureManageProfileFormKey);


export const selectNameForm = createSelector(featureMangeProfileForm, (state: any) => state.nameForm);
export const selectPermForm = createSelector(featureMangeProfileForm, (state: any) => state.perms);
export const selectProfileForm = createSelector(featureMangeProfileForm, (state: any) => ({
  perms: state.perms,
  nameForm: state.nameForm
}));
export const selectProfileId = createSelector(featureMangeProfileForm, (state: any) => state.profileId);
export const selectKeepTotalCount = createSelector(featureMangeProfileForm, (state: any) => state?.keepTotalCount || 0);
export const selectKeepOldProfileData = createSelector(featureMangeProfileForm, (state: any) => state?.keepOldProfileData);
export const selectUserCreatedBy = createSelector(featureMangeProfileForm, (state: any) => state?.createdBy);
export const selectUserOwnerId = createSelector(featureMangeProfileForm, (state: any) => state?.ownerId);

export const selectClientProfilleForm = createSelector(featureMangeProfileForm, (state: any) => state.client);
export const selectClientId = createSelector(featureMangeProfileForm, (state: any) => state.nameForm?.clientId);
export const selectProfileLoadingComplete = createSelector(featureMangeProfileForm, (state: any) => state.loadingComplete);
