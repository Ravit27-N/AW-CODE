import { createAction, props } from '@ngrx/store';
import {
  FlowConfirmMessage,
  FlowFilterCriteriaParams,
  FlowTraceabilityList,
  FlowTraceabilityModel,
} from '../../models';
import { HttpErrorResponse } from '@angular/common/http';

export const loadFeatureFlowTraceabilityList = createAction(
  '[Flow traceability/Load feature flow traceability list]',
  props<{
    page: number;
    pageSize: number;
    params?: FlowFilterCriteriaParams;
    isLoading?: boolean;
  }>()
);

export const loadFeatureFlowTraceabilityListSuccess = createAction(
  '[Flow traceability/Load feature flow traceability list success]',
  props<{
    response: FlowTraceabilityList;
    params?: FlowFilterCriteriaParams;
  }>()
);

export const loadFeatureFlowTraceabilityListFail = createAction(
  '[Flow traceability/Load feature flow traceability list fail]',
  props<{
    error?: string;
  }>()
);

export const filterCriteriaFlowChangeAction = createAction(
  '[Flow traceability] / on filter criteria changed',
  props<{
    page?: number;
    pageSize?: number;
    params?: FlowFilterCriteriaParams;
  }>()
);

export const viewFlowHistoryDialog = createAction(
  '[Flow tracking/view flow history]',
  props<FlowTraceabilityModel>()
);

export const downloadFeedAction = createAction(
  '[Flow tracking/download feed]',
  props<{ flow: any, _type: string}>()
);

export const navigateToDocumentTraceabilityAction = createAction(
  '[Flow tracking/navigate flow documentTraceability]',
  props<FlowTraceabilityModel>()
);

export const removeState = createAction('[Flow tracking / unload form]');

// Cancel flow traceability
export const confirmCancelFlowTraceability = createAction(
  '[flow-traceability / confirm cancel flow traceability]',
  props<{ data: FlowConfirmMessage }>()
);
export const cancelFlowTraceabilityAfterConfirm = createAction(
  '[flow-traceability / cancel flow traceability after confirm]',
  props<{ flowId: number }>()
);
export const cancelFlowTraceabilityAfterConfirmSuccess = createAction(
  '[flow-traceability / cancel flow traceability after confirm success]',
  props<{ flowId: number }>()
);
export const cancelFlowTraceabilityAfterConfirmFail = createAction(
  '[flow-traceability / cancel flow traceability after confirm fail]',
  props<{ httpErrorResponse: HttpErrorResponse }>()
);

// Cancel flow.
export const cancelFlowDepositPortal = createAction(
  '[Flow-traceability] / cancel deposit flow',
  props<{ uuid: string; ownerId: number; flowId: number }>()
);
