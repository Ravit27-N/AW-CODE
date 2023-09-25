import { createReducer, on } from '@ngrx/store';
import * as actions from './flow-traceability.action';
import { FlowDocumentFilterCriteriaModel } from '../../models';

export const featureFlowTraceabilityKey = 'feature-flow-traceability';
const initialState = {
  subChannel: {},
  filterCriteria: {},
  users: {},
  flowFilterCriteriaParams: {},
  documentFilterCriteriaParams: {},
  isTabToDocument: false,
  documentFilterCriteria: {} as FlowDocumentFilterCriteriaModel,
};

export const featureFlowTraceabilityReducer = createReducer(
  initialState,
  on(actions.filterSubChannelSuccess, (state, props) => ({
    ...state,
    subChannel: props?.data?.subChannel,
    isLoading: true,
  })),
  on(actions.filterSubChannelFail, (state) => ({
    ...state,
    isLoading: false,
  })),
  on(actions.loadFlowTraceabilityFilterCriteriaSuccess, (state, props) => ({
    ...state,
    filterCriteria: props?.data,
    isLoading: false,
  })),
  on(actions.loadFlowTraceabilityFilterCriteriaFail, (state) => ({
    ...state,
    isLoading: false,
  })),
  on(actions.loadUserInServiceSuccess, (state, props) => ({
    ...state,
    users: props?.data,
    isLoading: false,
  })),
  on(actions.loadUserInServiceFail, (state) => ({
    ...state,
    isLoading: false,
  })),
  on(actions.tabToFlowDocument, (state, props) => ({
    ...state,
    isTabToDocument: props?.isToDocument,
    isLoading: false,
  })),
  on(actions.loadFlowDocumentFilterCriteriaSuccess, (state, props) => ({
    ...state,
    documentFilterCriteria: props?.data,
    isLoading: false,
  })),
  on(actions.loadFlowDocumentFilterCriteriaFail, (state) => ({
    ...state,
    isLoading: false,
  })),
  on(actions.clearFlowTraceabilityState, () => initialState)
);
