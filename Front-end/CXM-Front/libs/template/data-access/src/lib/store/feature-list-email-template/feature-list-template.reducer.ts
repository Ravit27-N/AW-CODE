import { createReducer, on } from '@ngrx/store';
import {
  loadFeatureListEmailTemplate,
  loadFeatureListEmailTemplateFailure,
  loadFeatureListEmailTemplateSuccess,
  unloadFeatureListEmailTemplate,
} from './feature-list-email-template.actions';
import { TemplateList } from '@cxm-smartflow/shared/data-access/model';

export const featureListEmailTemplateFeatureKey = 'feature-list-email-template';

export interface EmailTemplateListState {
  readonly [featureListEmailTemplateFeatureKey]: TemplateList;
}

export interface ItemState {
  response: TemplateList;
  isLoading: boolean;
  filters: any;
}

const initalEmailTemplateListState: ItemState = {
  response: {},
  isLoading: false,
  filters: {
    page: 1,
    pageSize: 11,
    sortByField: 'modelName',
    sortDirection: 'asc',
    templateType: '',
  },
};

export const featureListTemplateReducer = createReducer(
  initalEmailTemplateListState,
  on(loadFeatureListEmailTemplate, (state, props) => ({
    ...state,
    isLoading: true,
    filters: { ...state.filters, ...props },
  })),
  on(loadFeatureListEmailTemplateSuccess, (state, { response }) => ({
    ...state,
    response,
    isLoading: false,
  })),
  on(loadFeatureListEmailTemplateFailure, (state) => ({
    ...state,
    isLoading: false,
  })),
  on(unloadFeatureListEmailTemplate, () => ({
    ...initalEmailTemplateListState,
  }))
);
