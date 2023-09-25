import {
  loadCreateEmailCampaign,
  loadCreateEmailCampaignFail,
  loadCreateEmailCampaignSuccess
} from './feature-create-email-campaign.action';
import {createReducer, on} from "@ngrx/store";

export  const createEmailCampaignKey = 'CreateEmailCampaign';
const initialState = {};

export const createEmailCampaignReducer = createReducer(
  initialState,
  on(loadCreateEmailCampaign, (state, {isLoading}) => ({
    ...state,
    isLoading
  })),
  on(loadCreateEmailCampaignSuccess,(state, {response, isLoading}) => ({
    ...state,
    response,
    isLoading
  })),
  on(loadCreateEmailCampaignFail, (state, {error, isLoading}) => ({
    ...state,
    error,
    isLoading
  }))
)
