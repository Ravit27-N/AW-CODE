import { createAction, props } from '@ngrx/store';
import { TemplateModel } from '@cxm-smartflow/shared/data-access/model';
import { HttpErrorResponse } from '@angular/common/http';

export const loadTemplate = createAction(
  '[template / load by id]',
  props<{
    modelName: string;
    mode: string;
    id: number;
    templateType: string;
    sourceTemplateId: any;
  }>()
);

export const loadTemplateSuccess = createAction(
  '[template / load template success]',
  props<{
    modelName: string;
    vars: any;
    mode: string;
    templateType: string;
    id: number;
    htmlFile?: string;
    row?: any;
    ownerId?: number;
  }>()
);

export const loadTemplateFail = createAction(
  '[template / load template fail]',
  props<{ templateType: string, error: any }>()
);

export const userAccessDenied = createAction(
  '[template] / user don\'t have permission',
  props<{error?: any, templateType: string}>()
);

export const unloadSmsTemplate = createAction('[template sms / unload]');

export const addFormVar = createAction('[template sms / add var]');

export const removeFormVar = createAction(
  '[template sms / remove var]',
  props<{ index: number; varName: string }>()
);

export const submitFormVar = createAction(
  '[template sms / var form submit]',
  props<{ variables?: string[] }>()
);

export const submitFormVarSuccess = createAction(
  '[template sms / var form submit success]',
  props<{ mode: string; result: any }>()
);

export const submitFormVarFail = createAction(
  '[template sms / var form submit fail]',
  props<{ mode: string }>()
);

export const updateValueFormVar = createAction(
  '[template sms / update var]',
  props<{ index: number; value: string }>()
);

export const updateModelNameFormVar = createAction(
  '[tempalte sms / change model name]',
  props<{ value: string }>()
);

export const changeSmsTemplateText = createAction(
  '[template sms / change sms template value]',
  props<{ value: string; formHasChanged?: boolean }>()
);

export const attemptToSubmitSmsForm = createAction(
  '[template sms / attempt sms submit]'
);

export const navigateToList = createAction('[template sms / navigate to list]');

export const loadGraphJsAssets = createAction(
  '[template sms / load graph assets]'
);

export const loadGraphJsAssetsSuccess = createAction(
  '[template sms / load graph assets success]',
  props<{ assets: any }>()
);

export const setSMSFormHasChange = createAction(
  '[template sms / set form has changed]',
  props<{ changed: boolean }>()
);

export const modifyVariableForm = createAction(
  '[template sms / sms form dirty]'
);

export const modifyEmailEditor = createAction(
  '[template graph / modify email editor]'
);

export const templateFormChangeEvent = createAction(
  '[Template] / form change value event'
);

export const modelNameChangeEvent = createAction(
  '[Template] / model name change event',
  props<{
    value?: string;
    formHasChanged?: boolean;
    isDuplicate?: boolean;
    isRequired?: boolean;
    isMaxLength?: boolean;
  }>()
);

export const unloadTemplateForm = createAction(
  '[Template] / unload template form'
);

export const initFormChange = createAction(
  '[Template] / variable form change',
  props<{ hasChange: boolean }>()
);

export const createTemplateByDuplicate = createAction(
  '[Template] / create template by duplicate',
  props<{ template: TemplateModel }>()
);

export const editTemplate = createAction(
  '[Template] / edit template',
  props<{ template: TemplateModel }>()
);

export const modifiedTemplate = createAction(
  '[template] / modified template',
  props<{ template: TemplateModel }>()
);

export const loadEmailTemplateHTML = createAction(
  '[template] / load email template html',
  props<{emailTemplateHTML: string}>()
)

export const fetchTemplateDefaultVars = createAction('[template / fetch default vars]');
export const setTemplateDefaultVars = createAction('[template/ set default vars]', props<{ defaultVars: [] }>());
export const fetchSmsTemplateForm = createAction('[template / fetch sms template form]', props<{ smsTextField: string }>());
