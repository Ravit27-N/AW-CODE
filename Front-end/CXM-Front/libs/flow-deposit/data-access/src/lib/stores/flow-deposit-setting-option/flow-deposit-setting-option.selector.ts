import { createFeatureSelector, createSelector } from '@ngrx/store';
import { flowDepositSettingOptionKey } from './flow-deposit-setting-option.reducer';
import { FlowDepositSettingOptionInitialStateModel } from '@cxm-smartflow/flow-deposit/data-access';

const selectFeatureFlowDepositSettingOption =
  createFeatureSelector<FlowDepositSettingOptionInitialStateModel>(flowDepositSettingOptionKey);

// All states.
export const selectSettingOptionAllStates = createSelector(selectFeatureFlowDepositSettingOption, (state) => state);

// Add resource type.
export const selectSettingOptionAddResourceType = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.addResourceType);
export const selectSelectedAddResourceType = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.selectedAddResourceType);
export const selectResourceData = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.resources.contents);
export const selectSelectedResource = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.selectedResource);

// Uploading area.
export const selectIsHideUploadingArea = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.isHideUploadingArea);
export const selectFileUploadedFileName = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.fileName);
export const selectFileUploadedFileSize = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.fileSize);

// Add resource position.
export const selectSettingOptionPosition = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.attachmentPosition);
export const selectSelectedSettingOptionPosition = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.selectedAttachmentPosition);
export const selectPdfInBase64 = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.base64);

// Messages.
export const selectTranslationMessages = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.messages);

// Select attachment lists.
export const selectSignaturesList = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.signatures);
export const selectAttachmentList = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.attachments);
export const selectBackgroundList = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.backgrounds);

// Add option attributes.
export const selectCanAddSignature = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.canAddSignature);
export const selectCanAddBackground = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.canAddBackground);
export const selectCanAddAttachment = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.canAddAttachment);

export const selectIsValidSignature = createSelector(selectFeatureFlowDepositSettingOption,(state) => state.isValidSignature);

export const selectWatermark = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.watermarkAttribute);
export const selectCanAddWatermark = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.canAddWatermark);
export const selectWatermarkPosition = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.selectedWatermarkPosition);

export const selectPostalInfo = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.postalInfo);
export const selectPostalStatus = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.postalStatus);
export const selectAddressDestination = createSelector(selectFeatureFlowDepositSettingOption, (state) => state.addressDestination);
