import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { ApprovalService } from '../../services/approval.service';
import { catchError, exhaustMap, map } from 'rxjs/operators';
import { of } from 'rxjs';
import {
  loadRemainingDateShipment,
  loadRemainingDateShipmentFail,
  loadRemainingDateShipmentSuccess,
} from './remaining-shipment.action';

@Injectable()
export class RemainingShipmentEffect {
  loadRemainingDateShipmentEffect$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadRemainingDateShipment),
      exhaustMap(() => {
        return this.service.fetchRemainingDateShipment().pipe(
          map((data) =>
            loadRemainingDateShipmentSuccess({
              data,
            })
          ),
          catchError(() => of(loadRemainingDateShipmentFail()))
        );
      })
    )
  );

  constructor(private actions$: Actions, private service: ApprovalService) {}
}
