import { createAction, props } from '@ngrx/store';
import { HttpErrorResponse } from '@angular/common/http';

// Flow traceability detail.
export const loadFlowHistoryDetail = createAction('[flow-traceability / load flow history detail]', props<{ id: number }>());
export const loadFlowHistoryDetailSuccess = createAction('[flow-traceability / load flow history detail success]', props<{ flowTraceabilityDetail: any }>());
export const loadFlowHistoryDetailFail = createAction('[flow-traceability / load flow history detail fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Download flow deposit document.
export const downloadFlowTraceabilityDepositDocument = createAction('[flow-traceability / download flow traceability deposit document]', props<{ flowDeposit: any, _type: string }>());

// Confirm cancel flow.
export const confirmCancelDepositFlow = createAction('[flow-traceability / confirm cancel deposit flow]', props<{ confirmMessage: any }>());
export const cancelDepositFlowAfterConfirm = createAction('[flow-traceability / cancel deposit flow after confirm]', props<{ fileId: string, ownerId: number; flowId: number }>());
export const cancelDepositFlowAfterConfirmSuccess = createAction('[flow-traceability / cancel deposit flow after confirm success]');
export const cancelDepositFlowAfterConfirmFail = createAction('[flow-traceability / cancel deposit flow after confirm fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Back to list of flow.
export const backToListOfFlowTraceability = createAction('[flow-traceability / back to list of flow traceability]');
export const unloadFlowHistoryDetail = createAction('[flow-traceability / unload flow history detail]');

// To finalize flow.
export const confirmFinalizeFlowFromFlowHistoryDetail = createAction('[flow-traceability / confirm finalize flow from flow history detail]', props<{ confirmMessage: any }>());
export const finalizedFlowFromFlowHistoryDetail = createAction('[flow-traceability / finalized flow from flow history]', props<{ flow: any }>());

// Navigate to flow's list of documents
export const navigateToFlowListDocument = createAction('[flow-traceability / navigate to flow list document]', props<{ flow: any }>());

// Flow campaign detail
export const flowCampaignDetail = createAction('[flow-traceability / flow campaign detail]', props<{ flowId: number }>());
export const flowCampaignDetailSuccess = createAction('[flow-traceability / flow campaign detail success]', props<{ flow: any }>());
export const flowCampaignDetailFail = createAction('[flow-traceability / flow campaign detail fail]', props<{ httpErrorResponse: HttpErrorResponse }>());
