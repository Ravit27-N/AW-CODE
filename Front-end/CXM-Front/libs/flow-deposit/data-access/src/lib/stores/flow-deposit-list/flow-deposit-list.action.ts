import { createAction, props } from '@ngrx/store';
import { FlowDepositFilterCriteriaModel, FlowDepositList, FlowDepositModel } from '../../model';
import { FlowConfirmMessage } from '@cxm-smartflow/flow-traceability/data-access';
import { HttpErrorResponse } from '@angular/common/http';

export const loadFlowDepositList = createAction(
  '[Flow deposit] / load listing',
  props<{
    page: number,
    pageSize: number,
    params?: FlowDepositFilterCriteriaModel
  }>()
);

export const loadFlowDepositListSuccess = createAction(
  '[Flow deposit] / load listing success',
  props<{
    response: FlowDepositList,
    params?: FlowDepositFilterCriteriaModel,
    isLoading: boolean
  }>()
);

export const loadFlowDepositListFail = createAction(
  '[Flow deposit] / load listing fail',
  props<{
    error: string;
    isLoading: boolean;
  }>()
);

export const confirmDeleteFlowDeposit = createAction(
  '[Flow deposit] / show confirm delete flow',
  props<{ data: FlowConfirmMessage }>()
);

export const deleteFlowDeposit = createAction(
  '[Flow deposit] / delete flow deposit',
  props<{fileId: number | string, hideShowMessage?: boolean}>()
);

export const deleteFlowDepositSuccess = createAction(
  '[Flow deposit] / delete flow deposit success',
  props<{hideShowMessage?: boolean}>()
);

export const deleteFlowDepositFail = createAction(
  '[Flow deposit] / delete flow deposit fail',
  props<{httpErrorResponse: HttpErrorResponse, hideShowMessage?: boolean}>()
);

export const downloadFile = createAction(
  '[Flow deposit] / download deposit file',
  props<{ flowDeposit: any, _type: string}>()
);

export const clearFlowDepositListState = createAction(
  '[Flow deposit] / unload flow deposit list'
);

export const modifiedFlowDeposit = createAction(
  '[Flow deposit] / modified flow deposit',
  props<{ row: FlowDepositModel }>()
);

export const loadFilterCriteria = createAction('[Flow deposit] / load filter criteria');
export const loadFilterCriteriaSuccess = createAction('[Flow deposit / load filter criteria success]', props<{ response: any }>());
export const loadFilterCriteriaFail = createAction('[Flow deposit / load filter criteria fail]', props<{ error?: any }>());

export const loadAllUserByService = createAction('[Flow deposit] / load all users by service', props<{serviceId?: number}>());
export const loadAllUserByServiceSuccess = createAction('[Flow deposit] / load all users by service success', props<{response?: any}>());
export const loadAllUserByServiceFail = createAction('[Flow deposit] / load all users by service fail', props<{error?: any}>());

export const deleteFlowDepositAfterDocumentNoOK = createAction('[Flow deposit] / delete flow deposit after document no ok', props<{fileId: number | string}>())
export const deleteFlowDepositAfterDocumentNoOKSuccess = createAction('[Flow document] / delete flow deposit after document no ok success');
export const deleteFlowDepositAfterDocumentNoOKFail = createAction('[Flow document] / delete flow deposit after document no ok fail', props<{error?: any}>());
