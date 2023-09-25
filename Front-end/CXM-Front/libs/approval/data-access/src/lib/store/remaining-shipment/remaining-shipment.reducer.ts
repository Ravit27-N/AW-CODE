import { createReducer, on } from '@ngrx/store';
import {
  clearRemainingDateShipmentState,
  loadRemainingDateShipmentSuccess,
} from './remaining-shipment.action';
import { RemainingShipmentModel } from '../../models';

export const featureApprovalRemainingDateKey =
  'feature-approval-remaining-date-key';

export interface RemainingShipmentReducerState {
  remaining: RemainingShipmentModel;
}

const initialState: RemainingShipmentReducerState = {
  remaining: { startDate: '', total: 0, endDate: '', isLoaded: false },
};

export const remainingShipmentReducer = createReducer(
  initialState,
  on(loadRemainingDateShipmentSuccess, (state, props) => ({
    ...state,
    remaining: { ...props.data, isLoaded: true },
  })),
  on(clearRemainingDateShipmentState, () => ({ ...initialState }))
);
