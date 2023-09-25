import { createAction, props } from '@ngrx/store';

export const showCreateTemplatePopup = createAction(
  '[Cxm mail template / show create template popup]',
  props<{ modelType: string }>()
);
