import {
  featureApprovalRemainingDateKey,
  RemainingShipmentReducerState,
} from './remaining-shipment.reducer';
import { createFeatureSelector, createSelector } from '@ngrx/store';
import { RemainingShipmentModel } from '../../models';

const approvalRemainingShipmentFeatureSelector = createFeatureSelector<RemainingShipmentReducerState>(
  featureApprovalRemainingDateKey
);

export const selectRemainingShipmentDate = createSelector(
  approvalRemainingShipmentFeatureSelector,
  (state) => state.remaining as RemainingShipmentModel
);
