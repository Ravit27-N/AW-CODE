import { createAction, props } from "@ngrx/store";
import { HttpErrorResponse } from "@angular/common/http";
import {
  CreateEnvelopeReference,
  PageEnvelopeReference, ResponseEnvelopeReference,
  SearchPageEnvelopeReference,
  UpdateEnvelopeReference
} from "../models";


export const createEnvelopeReference = createAction('[cxm-setting / create envelope reference]', props<{ payload: CreateEnvelopeReference}>());
export const createEnvelopeReferenceSuccess = createAction('[cxm-setting / create envelope reference success]');
export const createEnvelopeReferenceFail = createAction('[cxm-setting / create envelope reference fail]',props<{ httpErrorResponse: HttpErrorResponse }>());

export const updateEnvelopeReference = createAction('[cxm-setting / update envelope reference]', props<{ payload: UpdateEnvelopeReference}>());
export const updateEnvelopeReferences = createAction('[cxm-setting / update many envelope references]', props<{ payload: UpdateEnvelopeReference, ids: string[]}>());
export const updateEnvelopeReferenceSuccess = createAction('[cxm-setting /  update envelope reference success]');
export const updateEnvelopeReferenceFail = createAction('[cxm-setting /  update envelope reference fail]',props<{ httpErrorResponse: HttpErrorResponse }>());

export const deleteEnvelopeReference = createAction('[cxm-setting /  delete envelope reference]', props<{ id: number }>());
export const deleteEnvelopeReferences = createAction('[cxm-setting /  delete envelope references]', props<{ ids: string[] }>());
export const deleteEnvelopeReferenceSuccess = createAction('[cxm-setting / delete envelope reference success]');
export const deleteEnvelopeReferenceFail = createAction('[cxm-setting / delete envelope reference fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

export const fetchEnvelopeReferences = createAction('[cxm-setting / fetch  envelope references]', props<{payload: SearchPageEnvelopeReference}>());
export const fetchEnvelopeReferencesSuccess = createAction('[cxm-setting / fetch  envelope references success]', props<{payload: PageEnvelopeReference}>());
export const fetchEnvelopeReferencesFail = createAction('[cxm-setting / fetch  envelope references fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

export const fetchEnvelopeReferenceById = createAction('[cxm-setting / fetch  envelope reference by ID]', props<{ id: number }>());
export const fetchEnvelopeReferenceByIdSuccess = createAction('[cxm-setting / fetch  envelope reference by ID success]',props<{payload: ResponseEnvelopeReference}>());
export const fetchEnvelopeReferenceByIdFail = createAction('[cxm-setting / fetch  envelope reference by ID fail]', props<{ httpErrorResponse: HttpErrorResponse }>());


export const openSelectionPanel = createAction('[cxm-setting / open  envelope reference  selection]');
export const closeSelectionPanel = createAction('[cxm-setting / close  envelope reference selection]');
export const setSelectionPanel = createAction('[cxm-setting / set  envelope reference selection]', props<{ active: boolean }>());

export const entriesBatchOfModification = createAction('[cxm-setting  / entries batch of modification  envelope reference]', props<{ modificationBatchId: string [], erList: ResponseEnvelopeReference[] }>());
export const mapBatchOfModification = createAction('[cxm-setting  / map batch of modification  envelope reference', props<{ filteredModified: string[] }>());

export const unloadEnvelopeReferencelist = createAction('[cxm setting / unload envelope reference list]');



