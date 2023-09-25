import { createFeatureSelector, createSelector } from '@ngrx/store';
import {setFeaturedSettingFeatureKey} from './feature-setting.reducer';

export const getSetFeatureSettingState = createFeatureSelector<any>(
  setFeaturedSettingFeatureKey
);

export const getSetFeaturedSetting = createSelector(getSetFeatureSettingState, (response) => {
  return response;
})
