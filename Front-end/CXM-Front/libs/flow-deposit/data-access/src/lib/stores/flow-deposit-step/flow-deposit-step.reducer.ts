import {createReducer, on} from "@ngrx/store";
import * as actions from './flow-deposit-step.action';

export const featureFlowDepositStepKey = 'FEATURE_FLOW_DEPOSIT_STEP_KEY';

const initialState = {
  show: false,
  steps: [
    { link: 'cxm-deposit/acquisition', name: 'flow.deposit.stepper.acquisition', completed: true, active: true, step: 1 },
    { link: 'cxm-deposit/pre-analysis', name: 'flow.deposit.stepper.choiceChannel',completed: false, active: false, step: 2  },
    { link: 'cxm-deposit/analysis-result', name: 'flow.deposit.stepper.analyze', completed: false, active: false, step: 3 },
    { link: 'cxm-deposit/production-criteria', name: 'flow.deposit.stepper.setting', completed: false, active: false, step: 4 },
    { link: 'cxm-deposit/finished', name: 'flow.deposit.stepper.mail', completed: false, active: false, step: 5 },
  ],
  stepActive: 1
}

export const flowDepositStepReducer = createReducer(
  initialState,
  on(actions.stepOnFlowDeposit, (state, props) => {
    let { steps } = state;
    steps = Array.from(steps).map((item, index) => index === Math.max(0, props.step - 1) ? { ...item, active: true } : { ...item, active: false })
    return { ...state, steps, stepActive: props?.step };
  }),
  on(actions.stepOnFlowDepositComplete, (state) => {
    let { steps } = state;
    steps = Array.from(steps).map((item) => item.active ? { ...item, completed: true } : item);
    return { ...state, steps };
  }),
  on(actions.stepOnReset, (state) => ({ ...initialState })),
  on(actions.stepOnPreloadFlowDeposit, (state) => {
    let { steps } = state;
    steps = Array.from(steps).map((item, index) => {
      return { ...item, completed: index <= 2, active: index === 2 };
    });

    return { ...state, steps }
  }),
  on(actions.stepOnActivated, (state, props) => ({...state, show: props.active})),
  on(actions.initPortalStep, (state, props) => {
    let {steps} = state;
    steps = Array.from(steps).map((item, index) => index === Math.max(0, props.step - 1) ? {
      ...item,
      active: true
    } : {...item, active: false, completed: (item.step >= 2 && index < props?.step) ? true : item.step === 1 ? false : item.completed})
    return {...state, steps, stepActive: props?.step};
  }),
);
