import { createFeatureSelector, createSelector } from "@ngrx/store"
import { EmailCampaignFeatureKey } from './campaign.reducers';
import { CustomFileModel } from '@cxm-smartflow/shared/data-access/model';
import { MetadataResponseModel } from '../../models';

const campaignFeatureSelector = createFeatureSelector(EmailCampaignFeatureKey);

export const selectCsvAttemTracker = createSelector(campaignFeatureSelector, (state: any) => state.csvAttempt);

export const selectCsvState = createSelector(campaignFeatureSelector, (state: any) => state.csvData);

export const selectCsvError = createSelector(campaignFeatureSelector, (state: any) => state.csvError);

export const selectForm = createSelector(campaignFeatureSelector, (state: any) => state.templateId);

export const selectTemplateDetails = createSelector(campaignFeatureSelector, (state: any) => state.templateDetails);

export const selectCampaignDetail = createSelector(campaignFeatureSelector, (state: any) => state.campaign);

export const selectCsvBlob = createSelector(campaignFeatureSelector, (state: any) => state.csvBlob);

export const selectCsvFirstRecode = createSelector(campaignFeatureSelector, (state: any) => state?.csvFirstRecode);

export const selectIsLoadingCsvData = createSelector(campaignFeatureSelector, (state: any) => state?.csvData?.isLoading);

export const selectIsLoading = createSelector(campaignFeatureSelector, (state: any) => state?.isLoading);

export const selectEmailState = createSelector(campaignFeatureSelector, (state: any) => state); // never use this in component

export const selectEmailCsvRecord = createSelector(campaignFeatureSelector, (state: any) => state.emailRecord);

export const selectEmailCsvFilter = createSelector(campaignFeatureSelector, (state: any) => state.csvFilter);

export const selectEmailCsvForm = createSelector(campaignFeatureSelector, (state: any) => ({ checkSameMail: state.checkSameMail,hasHeader: state.hasHeader }))

export const select2Lockable = createSelector(campaignFeatureSelector, (state: any) => state.step2Lockable);

export const selectEmailLoading = createSelector(campaignFeatureSelector, (state: any) => state.isShowSpinner);



// uploading csv
export const selectUploadingProgresssion = createSelector(campaignFeatureSelector, (state: any) => state.uploadingState.progression);
// export const selectUploadingState = createSelector(campaignFeatureSelector, (state: any) => ({ sending: state.sending, done: state.done, errorName: state.errorName }));
export const selectUploadingState = createSelector(campaignFeatureSelector, (state: any) => state.uploadingState);

export const selectCsvUploadMaxFileSize = createSelector(campaignFeatureSelector, (state: any) => state.limitSize);

//attachments of email campaign parameter form.
export const selectAttachmentsUploaded = createSelector(campaignFeatureSelector, (state: any) => state?.attachments);
export const selectCampaignWithAttachments = createSelector(campaignFeatureSelector, (state: any) => ({
  campaign: state?.campaign,
  attachments: state?.attachments
}));
export const selectParameterFormTemporary = createSelector(campaignFeatureSelector, (state: any) => state?.parameterFormTemporary);
export const selectEmailTemplateSources = createSelector(campaignFeatureSelector, (state: any) => ({
  emailRecord: state.emailRecord,
  templateDetails: state.templateDetails,
  unsubscribeLink: state.unsubscribeLink
}));

export const selectEmailMetadata = createSelector(campaignFeatureSelector, (state: any) => state.metadataResponse as MetadataResponseModel);
