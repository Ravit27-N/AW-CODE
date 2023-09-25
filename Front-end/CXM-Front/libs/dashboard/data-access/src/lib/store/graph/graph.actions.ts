import { createAction, props } from "@ngrx/store";
import {
  IGraphCannelEnvoyResult,
  IGraphDepositModeResult,
  IGraphEvolutionResult,
  IGraphFlowTrackingResult,
  IUpdateUserObject,
  UserValidation
} from '../../models';


export const fetchGraph = createAction('[dashboard / dashboard fetch all graph ]');
export const filterGraphChanged = createAction('[dashboard / filter changed]', props<{ option: number, start: Date, end: Date }>());

export const fetchGraphChannel = createAction('[dashboard/ fetch graph channel]');
export const fetchGraphChannelSuccess = createAction('[dashboard / fetch graph channel success]', props<{ data: IGraphCannelEnvoyResult }>());
export const fetchGraphChannelFail = createAction('[dashboard / fetch graph channel fail]', props<{ error: any }>());

export const fetchGraphDepositMode = createAction('[dashboard / fetch graph deposit mode]');
export const fetchGraphDepositModeSuccess = createAction('[dashboard / fetch graph deposit mode success]', props<{ data: IGraphDepositModeResult }>());
export const fetchGraphDepositModeFail = createAction('[dashboard / fetch graph deposit mode fail]', props<{ error: any }>());


export const fetchGraphFlowTracking = createAction('[dashboard / fetch graph flow]');
export const fetchGraphFlowtrackingSuccess = createAction('[dashboard / fetch flow success]', props<{ data: IGraphFlowTrackingResult }>());
export const fetchGraphFlowtrackingFail = createAction('[dashboard / fetch flow fail]', props<{ error: any }>());


export const fetchGraphEvolution = createAction('[dashboard /fetch graph evolution]');
export const fetchGraphEvolutionSuccess = createAction('[dashboard / fetch evolution success]', props<{ data: IGraphEvolutionResult }>());
export const fetchGraphEvolutionFail = createAction('[dashboard / fetch graph evoltion fail]', props<{ error: any }>());

export const fetchUserGraphFilter = createAction('[dashboard / fetch user graph filter]');
export const fetchUserGraphFilterSuccess = createAction('[dashboard / user graph filter fail]', props<{ filter: IUpdateUserObject }>());

export const setUserGraphFilter = createAction('[dashboard / set graph filter fail]', props<{ filter: IUpdateUserObject }>());

export const graphNonceAction = createAction('[dashboard / graph nonce]');
