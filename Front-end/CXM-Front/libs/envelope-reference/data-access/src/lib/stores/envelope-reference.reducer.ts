import { createReducer, on } from '@ngrx/store';
import {
  closeSelectionPanel, entriesBatchOfModification, fetchEnvelopeReferences,
  fetchEnvelopeReferencesSuccess, mapBatchOfModification, openSelectionPanel, setSelectionPanel
} from "./envelope-reference.action";

export const FEATURE_ENVELOPE_REFERENCE_KEY = 'feature-envelope-reference-key';

const envelopeReferenceInitialState: any = {
  contents: [],
  isLoading: false,
  sortDirection: 'desc',
  sortByField: 'createdAt',
  selectionOpen: false,
  page: 1,
  pageSize: 10,
  total: 0,
  filter: '',
  envelopeReferenceIds: [],
  isFilterError: false,
  filteredModified: [],
}


export const envelopeReferenceReducer = createReducer(
  envelopeReferenceInitialState,
  on(fetchEnvelopeReferencesSuccess, (state, { payload }) => {
    return ({
      ...state,
      contents: payload.content,
      pageSize: payload.size,
      total: payload.totalElements,
      isLoading: false
    })
  }),
  on(fetchEnvelopeReferences, (state, { payload }) => {
    return ({
      ...state,
      filter: payload.keyword,
      isLoading: true
    })
  }),
  on(entriesBatchOfModification, (state, props) => {
    return ({
      ...state,
      modificationBatchId: props.modificationBatchId
    })
  }),
  on(mapBatchOfModification, (state, props) => ({
    ...state,
    filteredModified: props.filteredModified
  })),
  on(setSelectionPanel, (state, props) => ({ ...state, selectionOpen: props.active })),
  on(openSelectionPanel, (state) => ({ ...state, selectionOpen: true })),
  on(closeSelectionPanel, (state) => ({ ...state, selectionOpen: false })),
);
