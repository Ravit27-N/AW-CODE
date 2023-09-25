import { createReducer, on, props } from '@ngrx/store';
import { FlowDepositFilterCriteriaModel, FlowDepositList } from '@cxm-smartflow/flow-deposit/data-access';
import * as fromAction$ from './flow-deposit-list.action';

export const flowDepositListKey = 'FLOW_DEPOSIT_LIST_KEY';

const initializeState = {
  response: {} as FlowDepositList,
  error: '',
  loading: false,
  params: {},
  showSearchBoxTooltip: false,
  isFilterCriteriaNotFound: false,
  filterCriteria: {},
  users: []
};

export const flowDepositListReducer = createReducer(
  initializeState,
  on(fromAction$.loadFlowDepositListSuccess, (state, props) => ({
    ...state,
    response: props?.response,
    params: props.params || {},
    showSearchBoxTooltip: props.response.total === 0 && (props.params?.filter || '').length !== 0,
    isFilterCriteriaNotFound: props.response.total === 0 && validateParams(props.params || {}),
    loading: props.isLoading
  })),
  on(fromAction$.loadFlowDepositListFail, (state, props) => ({
    ...state,
    response: {},
    error: props.error,
    loading: props.isLoading
  })),
  on(fromAction$.clearFlowDepositListState, () => ({
    ...initializeState
  })),
  on(fromAction$.loadFilterCriteriaSuccess, (state, props) => ({
    ...state,
    filterCriteria: props?.response
  })),
  on(fromAction$.loadFilterCriteriaFail, (state, props) => ({
    ...state,
    filterCriteria: props?.error
  })),
  on(fromAction$.loadAllUserByServiceSuccess, (state, props) => ({
    ...state,
    users: props.response
  }))
);

export const validateParams = (params: FlowDepositFilterCriteriaModel): boolean => {
  if (Object.keys(params).length !== 0) {
    return !!(
      (params.channels && params.channels.length !== 0) ||
      (params.subChannels && params.subChannels.length !== 0) ||
      (params.users && params.users.length !== 0)
    );
  }

  return false;
};
