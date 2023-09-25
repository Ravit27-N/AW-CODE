import { createAction, props } from '@ngrx/store';
import { CommunicationInteractiveResponse } from '../models';

// Load list of template.
export const loadCommunicationTemplate = createAction('[cxm communication interaction / load list of template]');
export const loadCommunicationTemplateSuccess = createAction('[cxm communication interaction / load list of template success]', props<{ communicationResponse: CommunicationInteractiveResponse }>());
export const loadCommunicationTemplateFail = createAction('[cxm communication interaction / load list of template fail]');

// Get remoted URL.
export const getRemotedUrl = createAction('[cxm communication interaction / get remoted url]', props<{ id: number }>());
export const getRemotedUrlSuccess = createAction('[cxm communication interaction / get remoted url success]', props<{ url: string }>());
export const getRemotedUrlFail = createAction('[cxm communication interaction / get remoted url fail]', props<{ httpError: any }>());
export const invalidateRemoteUrl = createAction('[cxm communication interaction / invalid remote url]');

export const loadCommunicationFilterChanged = createAction('[cxm communication interaction / filter list changed]',
props<{ filters: { filter: string } }>());

export const unloadFormCIform = createAction('[cxm communication interaction / unload form]');
