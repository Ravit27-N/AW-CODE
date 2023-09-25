import { createAction, props } from '@ngrx/store';
import {CampaignModel} from "../../models";

export const loadCreateEmailCampaign = createAction(
  '[Email campaign/Create Email Campaign]',
  props<{
    emailCampaign: CampaignModel,
    isLoading: boolean
  }>()
);

export const  loadCreateEmailCampaignSuccess = createAction(
  '[Email campaign/Create Email Campaign Success]',
  props<{
    response: CampaignModel,
    isLoading: boolean
  }>()
);

export const  loadCreateEmailCampaignFail = createAction(
  '[Email campaign/Create Email Campaign Fail]',
  props<{
    error?: string,
    isLoading: boolean
  }>()
);

