import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import * as actions from '../flow-deposit/flow-deposit.action';
import { switchMap, take, tap } from 'rxjs/operators';
import { of } from 'rxjs';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { navigateToStep, stepOnFlowDepositComplete, stepOnReset } from './flow-deposit-step.action';
import { depositUrlParams } from '../flow-deposit';

@Injectable()
export class FlowDepositStepEffect
{
  submitDestinationOnStepEffect$ = createEffect(() => this.actions$.pipe(
    ofType(actions.launchProcessControlSuccess, actions.analyseFlowSuccess, actions.switchFlowSuccess, actions.treatmentFlowSuccess),
    switchMap(args => of(stepOnFlowDepositComplete()))
  ))

  unloadFormEffect$ = createEffect(() => this.actions$.pipe(
    ofType(actions.clearDepositFlow),
    switchMap(args => of(stepOnReset()))
  ))

  navigateStepEffect$ = createEffect(() => this.actions$.pipe(
    ofType(navigateToStep),
    tap((args) => {
      const { step } = args;
      this.store.select(depositUrlParams).pipe(take(1)).subscribe(v => {
        if (v) {
          const { composedFileId, fileId, validation, ownerId } = v;
          const params = { ownerId, composedFileId, step: step.step, fileId, validation}
          if (!step?.link?.includes('cxm-deposit/acquisition')) {
            this.router.navigate([step.link], {queryParams: params});
          }
        }
      })
    })
  ), { dispatch: false } )

  constructor(private actions$: Actions, private store: Store, private router: Router) { }

}
