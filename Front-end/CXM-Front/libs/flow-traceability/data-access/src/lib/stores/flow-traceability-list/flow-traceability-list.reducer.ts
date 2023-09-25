import { createReducer, on } from '@ngrx/store';
import {
  cancelFlowTraceabilityAfterConfirmSuccess,
  loadFeatureFlowTraceabilityList,
  loadFeatureFlowTraceabilityListFail,
  loadFeatureFlowTraceabilityListSuccess,
  removeState,
} from './flow-traceability-list.action';
import { FlowTraceabilityList, validateParams } from '../../models';
import { FlowStatusConstant } from '@cxm-smartflow/flow-traceability/util';

export const featureFlowTraceabilityListKey = 'feature-flow-traceability-list';
const initialState = {
  flowTraceability: {
    contents: [],
    isLoading: false,
    page: 1,
    pageSize: 10,
    total: 0,
  } as FlowTraceabilityList,
  params: {},
  showSearchBoxTooltip: false,
  isLoading: true,
  isFilterCriteriaNotFound: false,
};

export const featureFlowTraceabilityListReducer = createReducer(
  initialState,
  on(loadFeatureFlowTraceabilityList, (state) => ({
    ...state,
    isLoading: true,
  })),
  on(loadFeatureFlowTraceabilityListSuccess, (state, { response, params }) => ({
    ...state,
    flowTraceability: response,
    params: params || {},
    showSearchBoxTooltip:
      response.total === 0 && (params?.filter || '').length !== 0,
    isLoading: false,
    isFilterCriteriaNotFound:
      response.total === 0 && validateParams(params || {}),
  })),
  on(cancelFlowTraceabilityAfterConfirmSuccess, (state, { flowId }) => {
    const flowStatus = {
      statusLabel: 'flow.traceability.status.canceled',
      status: FlowStatusConstant.CANCELED,
    };
    // update record of flow traceability after cancel.
    const contents = state.flowTraceability.contents?.map((item) => {
      if (item.id === flowId) {
        return {
          ...item,
          status: FlowStatusConstant.CANCELED,
          flowStatus,
        };
      }
      return item;
    });
    return {
      ...state,
      flowTraceability: {
        ...state.flowTraceability,
        contents: contents || [],
      },
    };
  }),
  on(loadFeatureFlowTraceabilityListFail, (state, { error }) => ({
    ...state,
    error: error,
    isLoading: false,
  })),
  on(removeState, () => initialState)
);
