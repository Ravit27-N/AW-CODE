import { createReducer, on } from '@ngrx/store';
import * as fromActions from './template-form.actions';

export const featureTemplateFormKey = 'feature-template-form-key';

const initialData = {
  id: 0,
  source: 0,
  modelName: '',
  vars: [''],
  templateType: '',
  htmlFile: '',
  attempt: 0,
  mode: '',
  assets: [],
  ownerId: 0,
  formHasChanged: false,
  showSpinner: false,
  isModelNameDuplicate: false,
  isModelNameRequired: false,
  isModelNameMaxLength: false,
  defaultVars: [],
  smsTextField: ''
};

export const smsTemplateReducer = createReducer(
  initialData,
  on(fromActions.loadTemplate, (state, props) => ({
    ...state,
    modelName: props.modelName,
    mode: props.mode,
    id: props.id,
    showSpinner: true
  })),
  on(fromActions.loadTemplateSuccess, (state, props) => ({
    ...state,
    modelName: props.modelName,
    vars: props.vars,
    templateType: props.templateType,
    htmlFile: <string>props.htmlFile,
    showSpinner: false,
    ownerId: props.ownerId || 0
  })),
  on(fromActions.unloadSmsTemplate, () => ({ ...initialData })),
  on(fromActions.addFormVar, (state) => ({
    ...state,
    vars: [...state.vars, '']
  })),
  on(fromActions.removeFormVar, (state, props) => {
    const vars = Array.from(state.vars).filter(
      (x, index) => index != props.index
    );
    return { ...state, vars };
  }),
  on(fromActions.updateValueFormVar, (state, { index, value }) => {
    const vars = Array.from(state.vars).map((item, i) =>
      i === index ? value : item
    );
    return { ...state, vars };
  }),
  on(fromActions.updateModelNameFormVar, (state, props) => ({
    ...state,
    modelName: props.value
  })),
  on(fromActions.changeSmsTemplateText, (state, props) => ({
    ...state,
    htmlFile: props.value,
    formHasChanged: props.formHasChanged || false
  })),
  on(fromActions.loadGraphJsAssetsSuccess, (state, props) => ({
    ...state,
    assets: props.assets
  })),
  on(fromActions.setSMSFormHasChange, (state, props) => ({
    ...state,
    formHasChanged: props.changed
  })),
  on(fromActions.submitFormVar, (state) => ({ ...state, showSpinner: true })),
  on(fromActions.submitFormVarSuccess, (state, props) => {
    if (props.mode === 'create')
      return {
        ...state,
        formHasChanged: false,
        showSpinner: false,
        id: props.result.id,
        mode: 'edit'
      };

    return { ...state, formHasChanged: false, showSpinner: false };
  }),
  on(fromActions.submitFormVarFail, (state) => ({
    ...state,
    showSpinner: false
  })),
  on(fromActions.modelNameChangeEvent, (state, props) => ({
    ...state,
    formHasChanged: props?.formHasChanged || false,
    modelName: props.value || '',
    isModelNameDuplicate: props?.isDuplicate || false,
    isModelNameRequired: props?.isRequired || false,
    isModelNameMaxLength: props?.isMaxLength || false
  })),
  on(fromActions.unloadTemplateForm, () => ({
    ...initialData
  })),
  on(fromActions.initFormChange, (state, props) => ({
    ...state,
    formHasChanged: props.hasChange
  })),
  on(fromActions.loadEmailTemplateHTML, (state, props) => ({
    ...state,
    emailTemplateHTML: props.emailTemplateHTML
  })),
  on(fromActions.setTemplateDefaultVars, (state, props) => ({ ...state, defaultVars: props.defaultVars })),
  on(fromActions.fetchSmsTemplateForm, (state, props) => ({
    ...state,
    smsTextField: props.smsTextField
  }))
);
