import {sendMailForm} from '@cxm-smartflow/shared/data-access/model';
import { createAction, props } from '@ngrx/store';


export const createFeaturedSetting = createAction(
  '[Manage my campaign/Create Featured Setting]',
  props<{
    data: sendMailForm,
    isLoading: any,
    status?: string
  }>()
)

export const createFeaturedSettingSuccess = createAction(
  '[Manage my campaign/Create Featured Setting Success]',
  props<{
    response?: any,
    isLoading: any,
    status?: string
  }>()
)

export const createFeaturedSettingFail = createAction(
  '[Manage my campaign/Create Featured Setting Fail]',
  props<{
    error?: string,
    isLoading: any,
    status?: string
  }>()
)

export const clearCreateFeatureSetting = createAction(
 '[Manage my campaign/Clear Feature Setting ]',
 props<{
   data?:sendMailForm,
   isLoading: any,
   status?: string
 }>()
);
