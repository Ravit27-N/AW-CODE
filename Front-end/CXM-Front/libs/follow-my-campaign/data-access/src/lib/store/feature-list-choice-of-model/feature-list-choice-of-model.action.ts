import { createAction, props } from '@ngrx/store';
import { TemplateList, TemplateModel } from '@cxm-smartflow/shared/data-access/model';
import { HttpErrorResponse } from '@angular/common/http';

export const loadListChoiceOfModelTemplate = createAction(
  'Follow my campaign / load list of model template',
  props<{
    page: number;
    pageSize: number;
    sortByField?: string;
    sortDirection?: string;
    filter?: string;
    templateType?: string;
  }>()
);

export const loadListChoiceOfModelTemplateSuccess = createAction(
  'Follow my campaign / load list of model template success',
  props<{ response: TemplateList, filter?: string;}>()
);

export const loadListChoiceOfModelTemplateFail = createAction(
  'Follow my campaign / load list of model template fail',
  props<{ error?:  HttpErrorResponse}>()
);

export const loadPreviewTemplate = createAction(
  '[Follow my campaign - preview choice of model]',
  props<{ emailTemplateModel: TemplateModel }>()
);
