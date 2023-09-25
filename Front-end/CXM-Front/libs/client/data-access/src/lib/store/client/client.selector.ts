import { createFeatureSelector, createSelector } from '@ngrx/store';
import { clientFeatureKey } from './client.reducer';

const featureClient = createFeatureSelector(clientFeatureKey);

export const selectClientsList = createSelector(featureClient, (state: any) => state.clients);
export const selectPagination = createSelector(featureClient, (state: any) => state.pagination);
export const selectFilters = createSelector(featureClient, (state: any) => state.filters);
