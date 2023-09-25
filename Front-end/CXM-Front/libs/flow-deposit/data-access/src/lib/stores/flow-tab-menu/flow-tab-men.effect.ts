import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { take, tap, withLatestFrom } from 'rxjs/operators';
import * as tabActions from './flow-tab-menu.action';
import { getTabState } from './flow-tab-menu.selector';
import { navigateToStep, selectFlowDepositStep } from '../flow-deposit-step';
import { goToFinishedPage } from './flow-tab-menu.action';


@Injectable()
export class TabMenuEffect {

  goBackTab$ = createEffect(() => this.actions$.pipe(
    ofType(tabActions.goBack),
    withLatestFrom(this.store$.select(getTabState)),
    tap(([args, menu]) => {
      if (menu.current?.parent) {
        if (!menu.current?.parent.link.includes('/cxm-deposit/acquisition')) {
          this.router.navigateByUrl(menu.current?.parent?.link);
        }
        this.store$.dispatch(tabActions.dequeTab(menu.current?.parent));
      }
    })
  ), { dispatch: false });

  goBackToPreAnalysis$ = createEffect(() => this.actions$.pipe(
      ofType(tabActions.goBackToPreAnalysis),
      tap(() => {
        // Navigate to step 2.
        this.store$.select(selectFlowDepositStep).pipe(take(1)).subscribe(v => {
          const step = v?.filter((e: any) => e?.step === 2)[0];
          this.store$.dispatch(navigateToStep({ step }));
        });
      })
    ),
    { dispatch: false }
  );

  goBackToAnalysisResult$ = createEffect(() => this.actions$.pipe(
      ofType(tabActions.goBackToAnalysisResult),
      tap(() => {
        // Navigate to step 3.
        this.store$.select(selectFlowDepositStep).pipe(take(1)).subscribe(v => {
          const step = v?.filter((e: any) => e?.step === 3)[0];
          this.store$.dispatch(navigateToStep({ step }));
        });
      })
    ),
    { dispatch: false }
  );

  goBackToProductCriterial$ = createEffect(() => this.actions$.pipe(
      ofType(tabActions.goBackProductCriterial),
      tap(() => {
        // Navigate to step 4.
        this.store$.select(selectFlowDepositStep).pipe(take(1)).subscribe(v => {
          const step = v?.filter((e: any) => e?.step === 4)[0];
          this.store$.dispatch(navigateToStep({ step }));
        });
      })
    ),
    { dispatch: false }
  );

  goBackToAcquisition$ = createEffect(() => this.actions$.pipe(
      ofType(tabActions.goBackAcquisition),
      tap(() => {
        // Navigate to step 1.
        this.store$.select(selectFlowDepositStep).pipe(take(1)).subscribe(v => {
          const step = v?.filter((e: any) => e?.step === 1)[0];
          this.store$.dispatch(navigateToStep({ step }));
        });
      })
    ),
    { dispatch: false }
  );

  goToFinishedPage$ = createEffect(() => this.actions$.pipe(
    ofType(tabActions.goToFinishedPage),
    tap(() => {
      // Navigate to step 5.
      this.store$.select(selectFlowDepositStep).pipe(take(1)).subscribe(v => {
        const step = v?.filter((e: any) => e?.step === 5)[0];
        this.store$.dispatch(navigateToStep({ step }));
      });
    })
  ), {dispatch: false})

  constructor(private actions$: Actions, private store$: Store, private router: Router) {

  }

}
