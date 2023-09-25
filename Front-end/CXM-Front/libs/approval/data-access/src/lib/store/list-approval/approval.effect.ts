import { Injectable } from '@angular/core';
import { FlowTraceabilityService } from '@cxm-smartflow/flow-traceability/data-access';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { forkJoin, of } from 'rxjs';
import {
  catchError,
  exhaustMap,
  map,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { ApprovalControlService } from '@cxm-smartflow/approval/data-access';
import { ApprovalService } from '../../services/approval.service';
import * as fromActions from './approval.action';
import * as fromSelector from './approval.selector';
import { StatusUtils } from '../../models/status.utils';

import * as moment from 'moment';


@Injectable()
export class ApprovalEffect {

  submitApproveEffect$ = createEffect(() => this.actions$.pipe(
    ofType(fromActions.submitApprove),
    exhaustMap((args) => {
      const { comment, flows } = args;
      return this.service.updateValidate(flows.map(x => x.fileId), StatusUtils.VALIDATED, comment).pipe(
        map(response => fromActions.submitValidateSuccess({ response, message: 'espace.selection.validate_success' })),
        catchError(err => of(fromActions.submitValidateFail({ httpError: err })))
      )
    })
  ))


  refuseApproveEffect$ = createEffect(() => this.actions$.pipe(
    ofType(fromActions.submitRefuse),
    exhaustMap((args) => {
      const { comment, flows } = args;

      return this.service.updateValidate(flows.map(x => x.fileId), StatusUtils.REFUSE_DOC, comment).pipe(
        map(response => fromActions.submitValidateSuccess({ response, message: 'espace.selection.validate_fail' })),
        catchError(err => of(fromActions.submitValidateFail({ httpError: err })))
      )
    })
  ))


  loadApproveFlowListEffect$ = createEffect(() => this.actions$.pipe(
    ofType(fromActions.loadFlowApproveList),
    withLatestFrom(this.store.select(fromSelector.selectApprovalFilterCriteria)),
    exhaustMap(([args, fcriteria]) => {
      const filters = this.prepareCriteria(args.filters, fcriteria);

      return this.service.getAll(filters).pipe(
        map(response => {
          // Add impact refuse/validate

          let { contents } = response;
          contents =  contents.map(x => Object.assign(x, { _editable: this.approvalService.checkIsCanValidateOrRefuse(x.ownerId) }));
          return fromActions.loadFlowAppproveListSuccess({ response: { ...response, contents } })
        }),
        catchError(() => [fromActions.loadFlowApproveListFail()]))
    })
  ))

  loadApproveFlowListFailEffect$ = createEffect(() => this.actions$.pipe(
    ofType(fromActions.loadFlowApproveListFail),
    tap(() => {
      this.translate.get('espace.errorMessage.failtoFetchList').toPromise().then(message => {
        this.snackbar.openCustomSnackbar({ type: 'error', icon: 'close', message })
      })
    })
  ), { dispatch: false })



    filterFlowFilterChange$ = createEffect(() => this.actions$.pipe(
      ofType(fromActions.filterFlowApproveChanged),
      switchMap(args => {
        localStorage.setItem('list-espace-flow', JSON.stringify(args.filters));
        return of(fromActions.loadFlowApproveList({ filters: args.filters }))
      })
    ))


  submitValidateSuccessEffect$ = createEffect(() => this.actions$.pipe(
    ofType(fromActions.submitValidateSuccess),
    withLatestFrom(this.store.select(fromSelector.selectApprovalFilter)),
    tap(([args, filters]) => {
      this.translate.get(args.message).toPromise().then(message => {
        this.snackbar.openCustomSnackbar({ icon: 'close', type: 'success', message: message });
      })

    }),
    switchMap(([args, filters]) => [fromActions.filterFlowApproveChanged({ filters })])
  ), {  dispatch: true })

  submitValidateFailEffect$ = createEffect(() => this.actions$.pipe(
    ofType(fromActions.submitValidateFail),
    tap(({ httpError }) => {
      const key = (httpError?.error?.apierrorhandler?.statusCode === 4003)?
        'espace.selection.not_yet_configure_unloading' : 'espace.selection.validate_error';
      this.translate.get(key).toPromise().then(message => {
        this.snackbar.openCustomSnackbar({  icon: 'close', type: 'error', message: message })
      })

    })
  ), { dispatch: false })


  loadCriteriaFilterEffect$ = createEffect(() => this.actions$.pipe(
    ofType(fromActions.loadCriteriaFilter),
    exhaustMap(args => {

      const requestCategory = this.traceabilityService.getFlowTraceabilityFilterCriteria();
      const requestUsers = this.traceabilityService.getUsers();
      const requestRemainingDate= this.service.fetchRemainingDateShipment();

      return forkJoin([requestCategory, requestUsers, requestRemainingDate]).pipe(
        map(([categoriesResponse, users, dates]) =>{
          const { subChannel } = categoriesResponse;
          return fromActions.loadCriteriaFilterSuccess( { users: users, categories: subChannel as any[], dates})
        }),
        catchError(() => [fromActions.loaCriteriaFail()])
      )
    })
  ))


  prepareCriteria(filters: any, fcriteria: any) {
    const { start, end, users, channels } = filters;
    let f = { ...filters };

    if(start) {
      f = { ...f, start: moment(start).format('yyyy-MM-DD') }
    }

    if(end) {
      f = { ...f, end: moment(end).format('yyyy-MM-DD') }
    }

    if(users) {
      f = { ...f, users: users.map((y: any) => y.id) }
    }

    if(channels && channels.length > 0) {
      f = { ...f, channels }
    }

    return f;
  }

  constructor(private actions$: Actions, private service: ApprovalService, private snackbar: SnackBarService,
    private store: Store,
    private translate: TranslateService,
    private approvalService: ApprovalControlService,
    private traceabilityService: FlowTraceabilityService
    ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }

}
