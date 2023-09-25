import { createAction, props } from '@ngrx/store';

export const deleteEmailTemplate = createAction(
  '[Email Template / Delete Email Template]',
  props<{
    id: number;
    isLoading: boolean;
    modelName: string;
  }>()
);

export const deleteEmailTemplateSuccess = createAction(
  '[Email Template / Delete Email Template Success]',
  props<{
    response?: any;
    isLoading: boolean;
    modelName: string;
  }>()
);

export const deleteEmailTemplateFail = createAction(
  '[Email Template / Delete Email Template Fail]',
  props<{
    response?: any;
    isLoading: boolean;
  }>()
);
