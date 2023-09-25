import { createReducer, on } from '@ngrx/store';
import {
  loadUpdateEmailCampaign,
  loadUpdateEmailCampaignFail,
  loadUpdateEmailCampaignSuccess,
  loadUpdateCsvEmailCampaign,
  loadUpdateValidationEmailCampaign,
} from './feature-update-email-campaign.action';

export const updateEmailCampaignKey = 'UpdateEmailCampaign';
const initialState = {};

export const updateEmailCampaignReducer = createReducer(
  initialState,
  on(loadUpdateEmailCampaign, (state, { isLoading }) => ({
    ...state,
    isLoading,
  })),
  on(loadUpdateCsvEmailCampaign, (state, { isLoading }) => ({
    ...state,
    isLoading,
  })),
  on(loadUpdateValidationEmailCampaign, (state, { isLoading }) => ({
    ...state,
    isLoading,
  })),
  on(loadUpdateEmailCampaignSuccess, (state, { response, isLoading }) => ({
    ...state,
    response,
    isLoading,
  })),
  on(loadUpdateEmailCampaignFail, (state, { error, isLoading }) => ({
    ...state,
    error,
    isLoading,
  }))
);
