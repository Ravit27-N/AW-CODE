import { createAction, props } from '@ngrx/store';
import { HttpErrorResponse } from '@angular/common/http';
import { EventHistoryInfo } from '@cxm-smartflow/flow-traceability/ui/featured-flow-event-history';

// Load document detail.
export const loadFlowDocumentDetail = createAction('[flow-document-detail / load flow document detail]', props<{ id: number}>());
export const loadFlowDocumentDetailSuccess = createAction('[flow-document-detail / load flow document detail success]', props<{ documentDetail: any}>());
export const loadFlowDocumentDetailFail = createAction('[flow-document-detail / load flow document detail fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Unload flow document detail.
export const unloadFlowDocumentDetail = createAction('[flow-document-detail / unload flow document detail]');

// Download document file.
export const downloadDocumentDetail = createAction('[flow document detail / download document detail]', props<{ documentId: string, docName: string, _type: string }>());

// Back to last URL.
export const backToLastURL = createAction('[flow document detail / back to last URL]');

// Load associate document
export const loadAssociateDocument = createAction('[flow document detail / load associate document]', props<{ flowDocumentId: number }>());
export const loadAssociateDocumentSuccess = createAction('[flow document detail / load associate document success]', props<{ associateDocument: any }>());
export const loadAssociateDocumentFail = createAction('[flow document detail / load associate document fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Download associate document
export const downloadAssociateDocument = createAction('flow document detail / download associate document', props<{ fileId: string, associateDocKey: string }>());

// View status info
export const viewStatusInfo = createAction('flow document detail / view status info', props<{ id: number, locale: string }>());
export const viewStatusInfoSuccess = createAction('flow document detail / view status info success', props<{ eventHistoryInfo: EventHistoryInfo }>());
export const viewStatusInfoFail = createAction('flow document detail / view status info fail', props<{ httpErrorResponse: any }>());
