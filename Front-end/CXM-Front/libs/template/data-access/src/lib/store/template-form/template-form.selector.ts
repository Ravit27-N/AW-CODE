import { featureTemplateFormKey } from './template-form.reducers';
import { createFeatureSelector, createSelector } from '@ngrx/store';

const smsTemplateFeature = createFeatureSelector<any>(featureTemplateFormKey);

export const selectTemplateForm = createSelector(
  smsTemplateFeature,
  ({ vars, modelName, mode }) => ({ vars, modelName, mode })
);

export const selectTemplateVariables = createSelector(
  smsTemplateFeature,
  (state: any) => state?.vars
);

export const selectTextSmsTemplate = createSelector(
  smsTemplateFeature,
  ({ template }) => ({ template })
);

export const selectTemplateFormState = createSelector(
  smsTemplateFeature,
  (state) => state
);

export const selectedAttempTracker = createSelector(
  smsTemplateFeature,
  (state) => state.attempt
);

export const selectedGraphAssets = createSelector(
  smsTemplateFeature,
  (state) => state.assets
);

export const selectFormHasChanged = createSelector(
  smsTemplateFeature,
  (state) => state.formHasChanged
);

export const selectHtmlFile = createSelector(
  smsTemplateFeature,
  (state) => state.htmlFile
);

export const selectShowSpinner = createSelector(
  smsTemplateFeature,
  (state) => state?.showSpinner
);

export const selectModelName = createSelector(
  smsTemplateFeature,
  (state: any) => state?.modelName
);

export const selectTemplateId = createSelector(
  smsTemplateFeature,
  (state: any) => state?.id
);

export const selectTemplateType = createSelector(
  smsTemplateFeature,
  (state: any) => state?.templateType
);

export const selectModelNameStatus = createSelector(
  smsTemplateFeature,
  ({isModelNameRequired, isModelNameDuplicate, isModelNameMaxLength}) => ({isModelNameRequired, isModelNameDuplicate, isModelNameMaxLength})
);

export const selectEmailTemplate = createSelector(
  smsTemplateFeature,
  (state) => (state?.emailTemplateHTML)
);

export const selectEmailTemplateDefaultVar = createSelector(smsTemplateFeature, (state) => state.defaultVars);
export const selectSmsForm = createSelector(smsTemplateFeature, (state) => state.smsTextField);
