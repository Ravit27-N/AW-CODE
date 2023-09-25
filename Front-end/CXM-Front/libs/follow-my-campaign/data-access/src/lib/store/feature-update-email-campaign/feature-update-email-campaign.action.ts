import { createAction, props } from '@ngrx/store';
import { CampaignModel } from '../../models';

export enum ActionType {
  LOAD_UPDATE = '[Email campaign] Update Email Campaign',
  LOAD_FAILURE = '[Email campaign] Update Email Campaign Failure',
  LOAD_SUCCESS = '[Email campaign] Update Email Campaign Success',
  LOAD_UPDATE_CSV = '[Email campaign] Update Csv Email Campaign',
  LOAD_UPDATE_FOR_VALIDATE = '[Email campaign] Update Email Campaign When Validation',
}

export const loadUpdateEmailCampaign = createAction(
  ActionType.LOAD_UPDATE,
  props<{
    emailCampaign: CampaignModel;
    isLoading: boolean;
  }>()
);

export const loadUpdateCsvEmailCampaign = createAction(
  ActionType.LOAD_UPDATE_CSV,
  props<{
    emailCampaign: CampaignModel;
    isLoading: boolean;
  }>()
);

export const loadUpdateValidationEmailCampaign = createAction(
  ActionType.LOAD_UPDATE_FOR_VALIDATE,
  props<{
    emailCampaign: CampaignModel;
    isLoading: boolean;
  }>()
);

export const loadUpdateEmailCampaignSuccess = createAction(
  ActionType.LOAD_SUCCESS,
  props<{
    response: CampaignModel;
    isLoading: boolean;
  }>()
);

export const loadUpdateEmailCampaignFail = createAction(
  ActionType.LOAD_FAILURE,
  props<{
    error?: string;
    isLoading: boolean;
  }>()
);
