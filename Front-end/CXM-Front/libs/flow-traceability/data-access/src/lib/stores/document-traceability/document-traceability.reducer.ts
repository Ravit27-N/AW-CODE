import { createReducer, on } from '@ngrx/store';
import * as fromDocumentTraceabilityAction from './document-traceability.action';
import {
  FlowDocumentList,
  FlowFilterCriteriaParams,
  validateParams,
} from '../../models';

export const featureDocumentTraceabilityListKey =
  'document-traceability-feature-key';

const initialState = {
  flowDocument: {
    contents: [],
    isLoading: false,
    page: 1,
    pageSize: 10,
    total: 0,
  } as FlowDocumentList,
  params: {} as FlowFilterCriteriaParams,
  flowTraceabilityId: 0,
  isLoading: true,
  showSearchBoxTooltip: false,
  isFilterCriteriaNotFound: false,
};

export const documentTraceabilityReducer = createReducer(
  initialState,
  on(fromDocumentTraceabilityAction.loadDocumentTraceabilityList, (state) => ({
    ...state,
    isLoading: true,
  })),
  on(
    fromDocumentTraceabilityAction.loadDocumentTraceabilityListSuccess,
    (state, { response, flowTraceabilityId, params }) => ({
      ...state,
      flowDocument: response,
      flowTraceabilityId: flowTraceabilityId || 0,
      params: params || {},
      showSearchBoxTooltip:
        response.total === 0 && (params?.filter || '').length !== 0,
      isFilterCriteriaNotFound:
        response.total === 0 && validateParams(params || {}),
      isLoading: false,
    })
  ),
  on(
    fromDocumentTraceabilityAction.loadDocumentTraceabilityListFail,
    (state) => ({ ...state, isLoading: false })
  ),
  on(
    fromDocumentTraceabilityAction.unloadDocumentTraceabilityList,
    () => initialState
  )
);
