import { createReducer, on } from '@ngrx/store';
import {
  deleteEmailTemplate,
  deleteEmailTemplateFail,
  deleteEmailTemplateSuccess,
} from './delete-email-template.action';

export const deleteEmailTemplateKey = 'DeleteEmailTemmplate';

const initialState = {};

export const deleteEmailTemplateReducer = createReducer(
  initialState,
  on(deleteEmailTemplate, (state, { id, isLoading }) => ({
    ...state,
    id: id,
    isLoading,
  })),
  on(deleteEmailTemplateSuccess, (state, { response, isLoading }) => ({
    ...state,
    response,
    isLoading,
  })),
  on(deleteEmailTemplateFail, (state, { response, isLoading }) => ({
    ...state,
    response,
    isLoading,
  }))
);
