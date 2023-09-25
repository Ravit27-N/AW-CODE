import { createFeatureSelector, createSelector } from '@ngrx/store';
import { updateEmailCampaignKey } from './feature-update-email-campaign.reducer';

export const getUpdateEmailCampaignState = createFeatureSelector<any>(
  updateEmailCampaignKey
);

export const getUpdateEmailCampaign = createSelector(
  getUpdateEmailCampaignState,
  (response) => {
    return response;
  }
);
