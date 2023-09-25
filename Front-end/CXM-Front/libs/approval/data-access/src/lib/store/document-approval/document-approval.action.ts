import { createAction, props } from '@ngrx/store';
import { ApproveDocResponse } from '../../models';


export const loadApprovalDocumentList = createAction('[cxm approval doc / load document list by id]', props<{ id: number, filters: any, redirect?: boolean }>());
export const loadApprovalDocumentListSuccess = createAction('[cxm approval doc / load document list by id success]', props<{ response: ApproveDocResponse, redirectOnEmpty?: boolean }>());
export const loadApprovalDocumentListFail = createAction('[cxm approval doc / load document list by id fail]', props<{ httpError: any }>());


export const submitApproveDoc = createAction('[cxm approval doc / validate approve doc]', props<{ docs: any[], comment: string, flowId: number }>());
export const submitRefuseDoc = createAction('[cxm approval doc / refuse approve doc]', props<{ docs: any[], comment: string, flowId: number }>());
export const submitValidationSuccess = createAction('[cxm approval doc / submit success ]', props<{ response: any, message: string, flowId: number }>());
export const submitValidationFail = createAction('[cxm approval doc / submit fail ]', props<{ httpError: any }>());


export const openApprovalDocPanel = createAction('[cxm approval doc / open panel]');
export const closeApprovalDocPanel = createAction('[cxm approval doc / close panel]');


export const unloadApproveDoc = createAction('[cxm approval doc / unload]');

export const filterFlowApproveChanged  = createAction('[cxm approval doc / filter changed]', props<{ filters: any, id: number }>());

export const setFlowName = createAction('[cxm approval doc / set flow name]', props<{ name: string }>());

export const downloadFile = createAction('[cxm approval doc] / download file', props<{fileId: string, filename: string}>());
