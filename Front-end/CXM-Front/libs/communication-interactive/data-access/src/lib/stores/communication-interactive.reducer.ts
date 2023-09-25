import { createReducer, on } from '@ngrx/store';
import { CommunicationInteractiveResponse } from '../models/communication-interactive.response';
import {getRemotedUrlSuccess, loadCommunicationTemplateSuccess, unloadFormCIform} from './communication-interactive.action';

export const initialState: {
  response: CommunicationInteractiveResponse,
  url: string
 } = {
  response: { contents: [], page: 1, pageSize: 0, total: 0 },
  url: ''
}


export const communicationInteractiveReducerKey = 'communication-interactive-reducer-key';
export const communicationInteractiveReducer = createReducer(
  initialState,
  on(loadCommunicationTemplateSuccess, (state, props) => ({
    ...state,
    response: props.communicationResponse
  })),
  on(getRemotedUrlSuccess, (state, props) => ({
    ...state,
    url: props.url
  })),
  on(unloadFormCIform, (state, props) => ({
    ...initialState,
  }))
);
