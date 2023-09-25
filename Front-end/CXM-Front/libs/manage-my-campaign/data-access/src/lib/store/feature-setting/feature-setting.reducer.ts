import { sendMailForm } from '@cxm-smartflow/shared/data-access/model';
import { createReducer, on } from '@ngrx/store';
import { createFeaturedSetting, createFeaturedSettingSuccess, createFeaturedSettingFail, clearCreateFeatureSetting } from './feature-setting.action';

export const setFeaturedSettingFeatureKey = 'FeaturedSetting';

const initialState: sendMailForm = {
  template: {
    name: ''
  },
  email: {
    isPublished: '',
    replyToAddress: ''
  },
  contacts: [
    {
      contact: {
        firstname: '',
        lastname: ''
      },
      tokens: {
        nom: '',
        prenom: '',
        email: '',
        webview_url: '',
        unsubscribe_url: ''
      }
    }
  ]
};

export const setFeaturedSettingReducer = createReducer(
  initialState,
  on(createFeaturedSetting, (state, { isLoading, status }) => ({
    ...state,
    isLoading,
    status
  })),
  on(createFeaturedSettingSuccess, (state, { response, isLoading }) => ({
    ...state,
    response,
    isLoading
  })),
  on(createFeaturedSettingFail, (state, { error, isLoading, status }) => ({
    ...state,
    error,
    isLoading,
    status
  })),
  on(clearCreateFeatureSetting, (state, {isLoading}) => ({
    ...state,
    isLoading
  }))

);

