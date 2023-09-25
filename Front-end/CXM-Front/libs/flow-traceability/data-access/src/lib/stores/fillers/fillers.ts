import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { createAction, createFeatureSelector, createReducer, createSelector, on, props } from "@ngrx/store";
import { exhaustMap, map } from "rxjs/operators";
import { FlowTraceabilityService } from "../../services";

export const featureClientFillerKey = 'feature-flow-client-fillers';
const selectClientFillerFeature = createFeatureSelector(featureClientFillerKey);


const initialState = {
  fillers: [], loaded: false
}

export const loadClientFillers = createAction('[cxm flow client filler / load]');
export const loadClientFillersSuccess = createAction('[cxm flow client filler / success]', props<{ filler: [] }>());
export const loadClientFillerFail = createAction('[cxm flow client filler / fail]');

export const selectFlowClientFiller = createSelector(selectClientFillerFeature, (state: any) => state);


export const featureFlowClientFillerReducer = createReducer(
  initialState,
  on(loadClientFillersSuccess, (state, props) => {
    return { ...state, fillers: props.filler, loaded: true }
  })
)

@Injectable()
export class FlowClientFillersEffect {

  loadClientFillerEffect$ = createEffect(() => this.actions.pipe(
    ofType(loadClientFillers),
    exhaustMap((args) => {
      return this.service.getClientFiller().pipe(
        map(response => loadClientFillersSuccess({ filler: response }))
      )
    })
  ))


  constructor(private actions: Actions, private service: FlowTraceabilityService) { }

}
