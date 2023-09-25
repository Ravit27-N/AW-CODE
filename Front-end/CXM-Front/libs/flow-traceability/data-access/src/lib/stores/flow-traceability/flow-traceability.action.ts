import {createAction, props} from '@ngrx/store';
import {HttpErrorResponse} from "@angular/common/http";
import {FlowDocumentFilterCriteriaModel} from "../../models";

// filter sub-channel
export const filterSubChannel = createAction('[Flow traceability] /filter sub-channel', props<{ channel?: string }>());
export const filterSubChannelSuccess = createAction('[Flow traceability] /filter sub-channel success', props<{ data: any }>());
export const filterSubChannelFail = createAction('[Flow traceability] /filter sub-channel fail', props<{ httpErrorResponse: HttpErrorResponse }>());

// Load flow traceability filter criteria
export const loadFlowTraceabilityFilterCriteria = createAction('[Flow traceability] /load flow traceability filter criteria');
export const loadFlowTraceabilityFilterCriteriaSuccess = createAction('[Flow traceability] /load flow traceability filter criteria success', props<{ data: any }>());
export const loadFlowTraceabilityFilterCriteriaFail = createAction('[Flow traceability] /load flow traceability filter criteria fail', props<{ httpErrorResponse: HttpErrorResponse }>());

// Load all users in service
export const loadUserInService = createAction('[Flow traceability] /load load all users in service');
export const loadUserInServiceSuccess = createAction('[Flow traceability] /load load all users in service success', props<{ data: any }>());
export const loadUserInServiceFail = createAction('[Flow traceability] /load load all users in service fail', props<{ httpErrorResponse: HttpErrorResponse }>());

export const tabToFlowDocument = createAction('[Flow traceability] / switch tab to flow document', props<{ isToDocument: boolean }>());

export const clearFlowTraceabilityState = createAction(
  '[Flow traceability] / clear flow traceability state of criteria and tab'
);

// Load flow traceability filter criteria
export const loadFlowDocumentFilterCriteria = createAction(
  '[Flow traceability] / load flow document filter criteria', props<{ channel: string }>()
);
export const loadFlowDocumentFilterCriteriaSuccess = createAction(
  '[Flow traceability] / load flow document filter criteria success',
  props<{ data: FlowDocumentFilterCriteriaModel }>()
);
export const loadFlowDocumentFilterCriteriaFail = createAction(
  '[Flow traceability] / load flow document filter criteria fail',
  props<{ httpErrorResponse: HttpErrorResponse }>()
);
