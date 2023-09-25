import { createFeatureSelector, createSelector } from '@ngrx/store';
import { communicationInteractiveReducerKey } from './communication-interactive.reducer';

const communicationInteractiveSelectorKey = createFeatureSelector<any>(communicationInteractiveReducerKey);
export const selectCommunicationResponse = createSelector(communicationInteractiveSelectorKey, (state) => state.response);
export const selectedRemotedUrl = createSelector(communicationInteractiveSelectorKey, (state) => state?.url);
