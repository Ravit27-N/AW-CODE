import {createFeatureSelector, createSelector} from '@ngrx/store';
import {featureFlowDepositStepKey} from "./flow-deposit-step.reducer";

const selectFeatureFlowDepositStep = createFeatureSelector(featureFlowDepositStepKey);
export const selectFlowDepositStep = createSelector(selectFeatureFlowDepositStep, (state: any) => state?.steps);
export const showStepSeq = createSelector(selectFeatureFlowDepositStep, (state: any) => state?.show);
export const getStepActive = createSelector(selectFeatureFlowDepositStep, (state: any) => state?.stepActive);
