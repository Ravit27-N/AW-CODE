import { createAction, createFeatureSelector, createReducer, createSelector, on, props } from "@ngrx/store";

export const featureFluxExt = 'feature-client-modify-flux-ext';
export const selectClientFluxExt = createSelector(createFeatureSelector(featureFluxExt), (state: any) => state);

const initialState = {
  data: { },
  preserved: false
}

export const preserveClientFormState = createAction('[cxm client / ext preserve state]',props<{ data: any }>());
export const clearPreserveState = createAction('[cxm client / ext preserve state clear]');

export const reducer = createReducer(initialState,
  on(preserveClientFormState, (state, props) => {
    return {...state, data: props.data, preserved: true }
  }),
  on(clearPreserveState, (state, props) => ({ ...initialState  }))
)
