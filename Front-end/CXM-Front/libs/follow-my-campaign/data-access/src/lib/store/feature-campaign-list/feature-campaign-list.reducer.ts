import { createReducer, on } from '@ngrx/store';
import { loadFeatureCampaignList, loadFeatureCampaignListFail, loadFeatureCampaignListSuccess } from './feature-campaign-list.action';
import * as fromAction from './feature-campaign-list.action';

export const featureCampaignListKey = 'feature-campaign-list';
const initialResponseState: any = {
  response: {},
  isLoading: false,
  campaignResponse: {},
  campaignListFilter: {
    page: 1,
    pageSize: 10,
    sortByField: 'createdAt',
    sortDirection: 'DESC',
    filter: '',
    _type: 'EMAIL,SMS',
    mode: 'Manual,Automated'
  }
}

export const featureCampaignListReducer = createReducer(
  initialResponseState,
  on(loadFeatureCampaignList, (state, props) => ({
    ...state,
    isLoading: true,
    campaignListFilter: {
      page: props?.page,
      pageSize: props?.pageSize,
      sortByField: props?.sortByField,
      sortDirection: props?.sortDirection,
      filter: props?.filter,
      _type: props?._type,
      mode: props?.mode
    }
  })),
  on(loadFeatureCampaignListSuccess, (state, {response, isLoading}) => ({
    ...state,
    response: response,
    isLoading: isLoading
  })),
  on(loadFeatureCampaignListFail, (state, {error, isLoading}) => ({
    ...state,
    error: error,
    isLoading: isLoading
  })),
  on(fromAction.cancelCampaign, (state) => ({
    ...state,
    isLoading: true
  })),
  on(fromAction.cancelCampaignSuccess, (state) => ({
    ...state,
    isLoading: false
  })),
  on(fromAction.cancelCampaignFail, (state) => ({
    ...state,
    isLoading: false
  }))
)
