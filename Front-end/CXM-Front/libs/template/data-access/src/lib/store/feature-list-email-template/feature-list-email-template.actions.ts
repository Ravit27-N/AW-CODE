import { createAction, props } from '@ngrx/store';
import {
  TemplateList,
  TemplateModel,
} from '@cxm-smartflow/shared/data-access/model';

export const loadFeatureListEmailTemplate = createAction(
  '[Feature list email template] load',
  props<{ page?: number; pageSize?: number; sortByField?: string; sortDirection?: string; filter?: string; templateType?: string; }>()
);

export const loadFeatureListChooseModelEmailTemplate = createAction(
  '[Feature list email template] load choose model',
  props<{
    page: number;
    pageSize: number;
    sortByField?: string;
    sortDirection?: string;
    filter?: string;
    templateType?: string;
  }>()
);

export const loadFeatureListEmailTemplateSuccess = createAction(
  '[Feature list email template] success',
  props<{
    response: TemplateList;
  }>()
);

export const loadFeatureListEmailTemplateFailure = createAction(
  '[Feature list email template] fail'
);

export const unloadFeatureListEmailTemplate = createAction(
  '[Feature list email template] unload'
);

export const listModelTemplateFilterChanged = createAction(
  '[Feature list email template / filter changed] ',
  props<{
    page: number;
    pageSize: number;
    sortByField: string;
    sortDirection: string;
    templateType: string;
  }>()
);

export const downloadTemplateAsFile = createAction(
  '[Feature list template / download template]',
  props<{ template: TemplateModel }>()
);
