import { createFeatureSelector, createSelector } from '@ngrx/store';
import { featureCampaignListKey } from './feature-campaign-list.reducer';
import { CampaignListFilter } from '../../models';

export const getFeatureCampaignListState = createFeatureSelector<any>(
  featureCampaignListKey
)

export const getFeatureCampaignList = createSelector(getFeatureCampaignListState, (state: any) => state);
export const selectListCampaign = createSelector(getFeatureCampaignList, (state: any) => state?.response);
export const selectListLoading = createSelector(getFeatureCampaignList, (state: any) => state?.isLoading);
export const selectCancelLoading = createSelector(getFeatureCampaignList, (state: any) => state?.isLoading);

export const selectCampaignFilterList = createSelector(getFeatureCampaignList, (state: any) => state?.campaignListFilter as CampaignListFilter);
