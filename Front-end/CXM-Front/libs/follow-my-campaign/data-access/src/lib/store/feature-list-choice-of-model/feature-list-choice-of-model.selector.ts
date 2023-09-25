import { createFeatureSelector, createSelector } from '@ngrx/store';
import { listChoiceOfTemplateModelKey } from './feature-list-choice-of-model.reducer';

const listChoiceOfModelSelector = createFeatureSelector<any>(listChoiceOfTemplateModelKey);

export const selectListChoiceOfModel = createSelector(listChoiceOfModelSelector,(state: any) => state?.response);
export const selectListWithFilter = createSelector(listChoiceOfModelSelector, (state: any) => {
  return {
    response: state?.response,
    filter: state?.filter
  };
});
