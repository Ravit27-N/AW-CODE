import { FlowTraceabilityService } from '../../services';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Router } from '@angular/router';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { TranslateService } from '@ngx-translate/core';
import * as actions from './flow-traceability.action';
import { catchError, exhaustMap, map, tap } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';

@Injectable({ providedIn: 'root' })
export class FeatureFlowTraceabilityEffect {
  confirmPopupLabel: any;

  constructor(
    private action$: Actions,
    private flowTraceabilityService: FlowTraceabilityService,
    private router: Router,
    private confirmMessage: ConfirmationMessageService,
    private translate: TranslateService,
    private messageService: SnackBarService,
    private store: Store
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
    this.translate
      .get('flowTraceability.confirmationPopUp')
      .subscribe((response) => (this.confirmPopupLabel = response));
  }

  filterSubChannel$ = createEffect(() =>
    this.action$.pipe(
      ofType(actions.filterSubChannel),
      exhaustMap((args) => {
        return this.flowTraceabilityService
          .getFlowTraceabilitySubChannel(args.channel || '')
          .pipe(
            map((response) =>
              actions.filterSubChannelSuccess({ data: response })
            ),
            catchError((httpErrorResponse) => [
              actions.filterSubChannelFail(httpErrorResponse),
            ])
          );
      })
    )
  );

  filterSubChannelSuccess$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.filterSubChannelSuccess),
        tap(() => {
          //
        })
      ),
    { dispatch: false }
  );

  filterSubChannelFail$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.filterSubChannelFail),
        tap(({ httpErrorResponse }) => {
          //
        })
      ),
    { dispatch: false }
  );

  loadFlowTraceabilityFilterCriteria$ = createEffect(() =>
    this.action$.pipe(
      ofType(actions.loadFlowTraceabilityFilterCriteria),
      exhaustMap(() => {
        return this.flowTraceabilityService
          .getFlowTraceabilityFilterCriteria()
          .pipe(
            map((response) =>
              actions.loadFlowTraceabilityFilterCriteriaSuccess({
                data: response,
              })
            ),
            catchError((httpErrorResponse) => [
              actions.loadFlowTraceabilityFilterCriteriaFail(httpErrorResponse),
            ])
          );
      })
    )
  );

  loadFlowTraceabilityFilterCriteriaSuccess$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.loadFlowTraceabilityFilterCriteriaSuccess),
        tap(() => {
          //
        })
      ),
    { dispatch: false }
  );

  loadFlowTraceabilityFilterCriteriaFail$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.loadFlowTraceabilityFilterCriteriaFail),
        tap(({ httpErrorResponse }) => {
          //
        })
      ),
    { dispatch: false }
  );

  loadUserInService$ = createEffect(() =>
    this.action$.pipe(
      ofType(actions.loadUserInService),
      exhaustMap(() => {
        return this.flowTraceabilityService.getUsers().pipe(
          map((response) =>
            actions.loadUserInServiceSuccess({ data: response })
          ),
          catchError((httpErrorResponse) => [
            actions.loadUserInServiceFail(httpErrorResponse),
          ])
        );
      })
    )
  );

  loadUserInServiceSuccess$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.loadUserInServiceSuccess),
        tap(() => {
          //
        })
      ),
    { dispatch: false }
  );

  loadUserInServiceFail$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.loadUserInServiceFail),
        tap(({ httpErrorResponse }) => {
          //
        })
      ),
    { dispatch: false }
  );

  loadFlowDocumentFilterCriteria$ = createEffect(() =>
    this.action$.pipe(
      ofType(actions.loadFlowDocumentFilterCriteria),
      exhaustMap((args) => {
        return this.flowTraceabilityService
          .getFlowDocumentFilterCriteria(args.channel)
          .pipe(
            map((response) =>
              actions.loadFlowDocumentFilterCriteriaSuccess({ data: response })
            ),
            catchError((httpErrorResponse) => [
              actions.loadFlowDocumentFilterCriteriaFail(httpErrorResponse),
            ])
          );
      })
    )
  );

  loadFlowDocumentFilterCriteriaSuccess$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.loadFlowDocumentFilterCriteriaSuccess),
        tap(() => {
          //
        })
      ),
    { dispatch: false }
  );

  loadFlowDocumentFilterCriteriaFail$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.loadFlowDocumentFilterCriteriaFail),
        tap(({ httpErrorResponse }) => {
          //
        })
      ),
    { dispatch: false }
  );
}
