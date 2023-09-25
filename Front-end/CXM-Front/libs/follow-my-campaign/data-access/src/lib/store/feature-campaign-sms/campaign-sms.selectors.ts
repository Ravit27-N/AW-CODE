import { createFeatureSelector, createSelector } from "@ngrx/store";
import { campaigSmsFeatureKey } from "./campaign-sms.reducers";
import { MetadataResponseModel } from '../../models';


const campaignSmsFeature = createFeatureSelector(campaigSmsFeatureKey);

export const selectCampaignSmsLoader = createSelector(campaignSmsFeature, (state: any) => ({ mode: state.mode, loading: state.loading }));

export const selectCampaignSmsTemplate = createSelector(campaignSmsFeature, (state: any) => state.template);

export const selectSmsCsvData = createSelector(campaignSmsFeature, (state: any) => state.csvData);

export const selectSmsCsvRecord = createSelector(campaignSmsFeature, (state: any) => state.smsRecord);

export const selectSmsFormValue = createSelector(campaignSmsFeature, (state: any) => ({ hasHeader: state.hasHeader, checkSameNumber: state.checkSameNumber }));

export const selectSmsNavigation = createSelector(campaignSmsFeature, (state: any) => state.navigation);

export const selectSmsState = createSelector(campaignSmsFeature, (state: any) => state); // should use with withlastestFrom in effect only

export const selectSmsCampaign = createSelector(campaignSmsFeature, (state: any) => state.campaign);

export const selectSmsCsvFilter = createSelector(campaignSmsFeature, (state: any) => state.csvFilter);

export const selectSmsBatDialog = createSelector(campaignSmsFeature, (state: any) => state.batDialog);

export const selectLoading = createSelector(campaignSmsFeature, (state: any) => state?.loading);

export const selectStep = createSelector(campaignSmsFeature, (state: any) => state?.step);

export const selectLockAbleForm = createSelector(campaignSmsFeature, (state: any) => state?.lockableForm);

export const selectCsvTotal = createSelector(campaignSmsFeature, (state: any) => state?.csvFilter?.total);

export const selectSmsCsvError = createSelector(campaignSmsFeature, (state: any) => state.csvError);
export const selectStep2Lockable = createSelector(campaignSmsFeature, (state: any) => state.step2Lockable);

export const selectSmsUploadingProgresssion = createSelector(campaignSmsFeature, (state: any) => state.uploadingState.progression);
export const selectSmsUploadingState = createSelector(campaignSmsFeature, (state: any) => state.uploadingState);
export const selectIsSMSloading = createSelector(campaignSmsFeature, (state: any) => state.isSMSloading);

export const selectSMSMetadata = createSelector(campaignSmsFeature, (state: any) => state.metadataResponse as MetadataResponseModel);
