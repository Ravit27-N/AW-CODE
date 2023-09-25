import { Injectable } from "@angular/core";
import { SnackBarService, UserProfileUtil } from '@cxm-smartflow/shared/data-access/services';
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { createAction, createFeatureSelector, createReducer, createSelector, on, props, Store } from "@ngrx/store"
import { TranslateService } from "@ngx-translate/core";
import { catchError, exhaustMap, map, switchMap, tap, withLatestFrom } from "rxjs/operators";
import { FlowDepositService } from "../../services/flow-deposit.service";
import { getbackgroundPerm } from "../permission-utils";
import { EnrichmentMailing } from '@cxm-smartflow/shared/data-access/model';
import { EnrichmentPrivilegeUtil } from '@cxm-smartflow/flow-deposit/util';

export const featureKey = 'flow-deposit-background-ext';
const featureBackgroundExt = createFeatureSelector(featureKey);

const perm = getbackgroundPerm();
const initialState: {
  background: any;
  config: {
    buttonAdd: boolean, bg: number[], hasMissing: boolean
  },
  perm: {add: boolean, delete: boolean, modify: boolean, library: boolean, single: boolean }
} = {
  background: [],
  config: {
    buttonAdd: true, bg: [], hasMissing: false
  },
  perm
}

export const selectBackhgroundList = createSelector(featureBackgroundExt, (state: any) => state);


export const removeBackground = createAction('[depoit flow background / remove]', props<{ background: any }>());
export const invalidateBackground = createAction('[deposit flow backgorund / remove success]');
const removeBackgroundFail = createAction('[deposit flow background / remove fail]');

export const editBackground = createAction('[depoit flow background / edit]', props<{ background: any }>())

export const fetchFlowBackgroundList = createAction('[deposit flow background / list]');
const fetchFlowBackgroundListSucces = createAction('[deposit flow  background / list success]', props<{ background: any[], config: any }>());
const fetchFlowBackgroundListFail = createAction('[flow deposit background / list fail]');
const httpErrorActions = createAction('[watermar form / http error]', props<{ httpError: any, scope: string }>());

export const reducer = createReducer(initialState,
  on(fetchFlowBackgroundListSucces, (state, props) => {
    const { config } = props;
    const background = props.background.map(backgroundItem => {
      const {modifyPrivKey, deletePrivKey} = EnrichmentPrivilegeUtil.getPrivillegKey(backgroundItem);

      const modifiable = UserProfileUtil.getInstance().canModify({
        func: EnrichmentMailing.CXM_ENRICHMENT_MAILING, priv: modifyPrivKey, ownerId: backgroundItem.ownerId, checkAdmin: false,
      });

      const deletable= UserProfileUtil.getInstance().canModify({
        func: EnrichmentMailing.CXM_ENRICHMENT_MAILING, priv: deletePrivKey, ownerId: backgroundItem.ownerId, checkAdmin: false,
      });
      return { ...backgroundItem, modifiable, deletable };
    });
    return { ...state, background, config }
  })
);

const backgroundPositionPolicy = (background: []) => {
  const bg = background.map((x: any) => {
    if(x.position === 'ALL_PAGES') return 6;
    else if(x.position === 'FIRST_PAGE') return 1;
    else if(x.position === 'NEXT_PAGES') return 2;
    else if(x.position === 'LAST_PAGE') return 3;
    else return 0;
  }) as number[];

  const buttonAdd = bg.reduce((p,c) => p + c, 0) < 6;
  const hasMissing = background.some((x: any) => x?.missing === true);

  return {
    bg,
    buttonAdd,
    hasMissing
  }
}

const sortBackground = (backgrounds: Array<any>): Array<any> => {
  return backgrounds
    .map(background => {
      switch (background.position) {
        case 'FIRST_PAGE': return { ...background, order: 1 };
        case 'NEXT_PAGES': return { ...background, order: 2 };
        case 'LAST_PAGE': return { ...background, order: 3 };
        case 'ALL_PAGES': return { ...background, order: 6 };
        default: return background;
      }
    })
    .sort((a, b) => (a.order - b.order))
    .map(background => {
      const { order, ...rest } = background;
      return rest;
    });
}

@Injectable()
export class DepositFlowBackgroundList {

  invalidateBackgroundListEffect$ = createEffect(() => this.actions.pipe(
    ofType(invalidateBackground),
    switchMap(args => [fetchFlowBackgroundList()])
  ))

  removeBackgroundEffct$ = createEffect(() => this.actions.pipe(
    ofType(removeBackground),
    exhaustMap((args) => {
      const { background } = args;
      return this.depositService.deleteFlowBackground(background.id).pipe(
        map(res => invalidateBackground()),
        catchError((err) => [httpErrorActions({ httpError: err, scope: 'remove' })])
      )
    })
  ))


  httpErrorEffect$ = createEffect(() => this.actions.pipe(
    ofType(httpErrorActions),
    tap((args) => {
      const { httpError, scope } = args;
      if(httpError && httpError.error && httpError.error.apierrorhandler) {
        const { apierrorhandler } = httpError.error;

        if([500].includes(apierrorhandler.statusCode)) {
          this.translate.get('background.errors').toPromise().then((messages: any) =>
          this.snackbar.openCustomSnackbar({ type: 'error', message: messages.unknown }))
        } else {
          if(scope === 'remove') {
            if([401, 403].includes(apierrorhandler.statusCode)) {
              this.translate.get('background.errors').toPromise().then((messages: any) =>
              this.snackbar.openCustomSnackbar({ type: 'error', message: messages.noDeletePermission }))
            }

            if([404].includes(apierrorhandler.statusCode)) {
              this.translate.get('background.errors').toPromise().then((messages: any) =>
              this.snackbar.openCustomSnackbar({ type: 'error', message: messages.deleteConflict }))
            }
          }

          if(scope === 'list') {
            if([400, 401, 403, 404].includes(apierrorhandler.statusCode)) {
              this.translate.get('background.errors').toPromise().then((messages: any) =>
              this.snackbar.openCustomSnackbar({ type: 'error', message: messages.failToFetchList }))
            }
          }
        }
      }
    })
  ), { dispatch: false });

  constructor(
      private actions: Actions,
      private depositService: FlowDepositService,
      private store: Store,
      private translate: TranslateService,
      private snackbar: SnackBarService) { }

}
