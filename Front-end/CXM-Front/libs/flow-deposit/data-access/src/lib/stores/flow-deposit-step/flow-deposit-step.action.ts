import {createAction, props} from '@ngrx/store';

export const stepOnFlowDeposit = createAction('[step flow deposit / step on]', props<{ step: number }>());
export const stepOnFlowDepositComplete = createAction('[step flow deposit / step on completed]');
export const stepOnReset = createAction('[step flow deposit / reset]');
export const stepOnPreloadFlowDeposit = createAction('[step flow deposit / step on preload]');
export const navigateToStep = createAction('[step flow deposit] / step on nav', props<{ step: any }>());
export const stepOnActivated = createAction('[step flow deposit / activate]', props<{ active: boolean}>());
export const initPortalStep = createAction('[step flow deposit / initial portal step]', props<{ step: number }>());
