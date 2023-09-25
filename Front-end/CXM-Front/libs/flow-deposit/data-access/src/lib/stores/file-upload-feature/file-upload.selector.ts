import { createFeatureSelector, createSelector } from '@ngrx/store';
import { acquisitionFileUploadFeatureKey } from './file-upload.reducer';


const fileUploadFeature = createFeatureSelector(acquisitionFileUploadFeatureKey);

export const selectFileUploadState = createSelector(
  fileUploadFeature,
  (state: any) => state
);

export const selectPreAnalysisState = createSelector(fileUploadFeature, (state: any) => state?.response);

export const selectDepositLocked = createSelector(fileUploadFeature, (state: any) => state?.locked);

export const getUuid = createSelector(fileUploadFeature, (state: any) => state?.response?.uuid);

export const selectFilenameAcquisitionFileUpload = createSelector(fileUploadFeature, (state: any) => state?.response?.fileName);

export const selectErrorStatus = createSelector(fileUploadFeature, (state: any) => state.error);

export const selectLoading = createSelector(fileUploadFeature, (state: any) => state?.loading);
