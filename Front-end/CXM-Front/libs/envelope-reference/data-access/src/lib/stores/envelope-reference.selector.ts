import {createFeatureSelector, createSelector} from "@ngrx/store";
import {FEATURE_ENVELOPE_REFERENCE_KEY} from "./envelope-reference.reducer";


const envelopeReferenceListSelector = createFeatureSelector<any>(FEATURE_ENVELOPE_REFERENCE_KEY);
export const selectEnvelopeReferencesList = createSelector(envelopeReferenceListSelector, (state) => state as any);
export const selectFilteredModified = createSelector(envelopeReferenceListSelector, (state) => ({ filteredModified: state.filteredModified }));
export const selectSelectionOpened = createSelector(envelopeReferenceListSelector, (state) => state.selectionOpen);
