import { createReducer, on } from '@ngrx/store';
import * as actions from './feature-list-choice-of-model.action';

export const listChoiceOfTemplateModelKey = 'list-choice-of-template-model';

const initialState = {
  response: {},
  loading: false,
  filters: {
    page: 1,
    pageSize: 11,
    sortByField: 'modelName',
    sortDirection: 'asc',
    templateType: ''
  }
};

export const listChoiceOfModelReducer = createReducer(
  initialState,
  on(actions.loadListChoiceOfModelTemplate, (state, props) => ({
    ...state,
    filters: { ...state.filters, ...props },
    loading: true
  })),
  on(actions.loadListChoiceOfModelTemplateSuccess, (state, props) => ({
    ...state,
    filter: props.filter,
    response: props.response,
    loading: false
  })),
  on(actions.loadListChoiceOfModelTemplateFail, (state) => ({
    ...state,
    response: {},
    loading: false
  }))
);
