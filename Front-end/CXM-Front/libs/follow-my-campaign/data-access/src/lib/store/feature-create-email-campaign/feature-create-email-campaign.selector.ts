import { createFeatureSelector, createSelector } from '@ngrx/store';
import {createEmailCampaignKey} from './feature-create-email-campaign.reducer';

export const getCreateEmailCampaignState = createFeatureSelector<any>(
  createEmailCampaignKey
);

export const getCreateEmailCampaign = createSelector(
  getCreateEmailCampaignState,
  (response) => {
    return response;
  }
)
