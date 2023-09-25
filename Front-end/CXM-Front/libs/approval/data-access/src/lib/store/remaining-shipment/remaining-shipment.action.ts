import { createAction, props } from '@ngrx/store';
import { RemainingShipmentModel } from '../../models';

export const loadRemainingDateShipment = createAction(
  '[cxm approval / load remaining date shipment]'
);
export const loadRemainingDateShipmentSuccess = createAction(
  '[cxm approval / load remaining date shipment success]',
  props<{
    data: RemainingShipmentModel;
  }>()
);
export const loadRemainingDateShipmentFail = createAction(
  '[cxm approval / load remaining date shipment fail]'
);

export const clearRemainingDateShipmentState = createAction(
  '[cxm approval / clear remaining date shipment state]'
);
