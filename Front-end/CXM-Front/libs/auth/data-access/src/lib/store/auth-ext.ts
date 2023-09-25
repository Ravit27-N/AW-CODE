// effect flow for proccessing login with token

import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { FlowDepositService } from "@cxm-smartflow/flow-deposit/data-access";
import { CxmSmartflowService } from "@cxm-smartflow/shared/data-access/api";
import { appRoute } from "@cxm-smartflow/shared/data-access/model";
import { SnackBarService } from "@cxm-smartflow/shared/data-access/services";
import { ConfirmationMessageService } from "@cxm-smartflow/shared/ui/comfirmation-message";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { createAction, props, Store } from "@ngrx/store";
import { TranslateService } from "@ngx-translate/core";
import { from, of } from "rxjs";
import { catchError, exhaustMap, map, switchMap, tap } from "rxjs/operators";
import { ExtAuthService } from "../services/auth-ext.service";


  export const loadImpersonateProfile = createAction('[cxm auth ext /  load impersonate]', props<{ returnTo: string }>());
  const impersonateSuccess = createAction('[cxm auth ext/ impersonate success]', props<{ result: any, returnTo: string }>());
  const impersonateFail = createAction('[cxm auth ext/ impersonate fail]', props<{ returnTo: string }>());
  const determinerReturnBack = createAction('[cxm auth ext / return]', props<{ returnTo: string }>());
  export const initializeIntegrationTicket = createAction('[cxm auth ext / validate ticket]', props<{ ticket: string }>())
  const validateTicketSuccess = createAction('[cxm auth ext / validate ticket success]', props<{ response: any }>());
  const validateTicketFail = createAction('[cxm auth ext / validate ticket fail]', props<{ httpError: any }>());
  const actionNonce = createAction('[cxm auth ext / nonce]');

@Injectable()
export class AuthExtEffect {


  loadImpersonateProfile = createEffect(() => this.actions$.pipe(
    ofType(loadImpersonateProfile),
    switchMap((args) => this.authService.getUserPrivileges()
    .pipe(
        map(result => impersonateSuccess({ result, returnTo: args.returnTo })),
        catchError(error => of(impersonateFail({ returnTo: args.returnTo })))
      ),
    )
  ))

  impersonateSuccessEffect$ = createEffect(() => this.actions$.pipe(
    ofType(impersonateSuccess),
    switchMap(args => {
      return this.authService.setUserPrivilegesToStorage(JSON.stringify(args.result)).pipe(
        map(() => determinerReturnBack({ returnTo: args.returnTo }))
      )
    })

  ), { dispatch: true })

  impersonateFailEffect$ = createEffect(() => this.actions$.pipe(
    ofType(impersonateFail),
    tap((args) => {
      this.authService.removeUserPrivilegesFromStorage();
      this.router.navigate(['/confirm-logout'], { queryParams: { returnTo: args.returnTo } });
    })
  ), { dispatch: false })


  determinerReturnBackEffect$ = createEffect(() => this.actions$.pipe(
    ofType(determinerReturnBack),
    tap(args => {
      this.router.navigateByUrl(args.returnTo);
    })
  ), { dispatch: false })


  initializeIntegrationTicketEffect$ = createEffect(() => this.actions$.pipe(
    ofType(initializeIntegrationTicket),
    exhaustMap(args => {
      return this.smartflowService.validateTicket(args.ticket).pipe(
        map(res => validateTicketSuccess({ response: res })),
        catchError((err) => of(validateTicketFail({ httpError: err })))
      )
    })
  ))

  validateTicketSuccessEffect$ = createEffect(() => this.actions$.pipe(
    ofType(validateTicketSuccess),
    switchMap(args => {
      const {
        valid, expired, flowUUID, flowToken,
        flowReferenceToken, expiresAt, refresh_expires_in
      } = args.response;

      // const returnTo = appRoute.cxmDeposit.navigateToPreAnalysis + `?fileId=${flowUUID}&step=2`;
      const returnTo = '/integration/flow/' + flowUUID;

      if(valid === false) {
        this.router.navigateByUrl('/');
        return of(actionNonce());
      }

      if(expired === true) {
        this.router.navigate(['/confirm-logout'], { queryParams: { returnTo } });
        return of(actionNonce());
      }

      // success login with token
      return from(this.authService.loginWithToken({ access_token: flowToken, refresh_token: flowReferenceToken, expireAt: expiresAt }))
      .pipe(() => from(this.authService.loadUserProfile()))
      .pipe(map(() => loadImpersonateProfile({ returnTo })))
    })
  ))

  validateTicketFailEffect$ = createEffect(() => this.actions$.pipe(
    ofType(validateTicketFail),
    tap(args => {
      const { apierrorhandler } = args.httpError.error;

      if ([403, 404, 401, 405].includes(apierrorhandler.statusCode)) {
        Promise.all([
          this.translate.get('template.message').toPromise(),
          this.translate.get('integrations').toPromise(),
        ])
        .then(([messageProps, integrations]) => {
          this.confirmMessageService.showConfirmationPopup(
            {
              icon: 'close',
              title:  messageProps.unauthorize,
              message:integrations.failtovalidate,
              cancelButton: messageProps.unauthorizeCancel,
              confirmButton: messageProps.unauthorizeLeave,
              type: 'Warning'
            }
          ).subscribe(() => this.router.navigateByUrl(appRoute.dashboard.baseRoot));
        })
      } else {
        this.translate.get('integrations.serverError').toPromise().then(message => {
          this.snackbar.openCustomSnackbar({ type: 'error', icon: 'close', message });
          this.router.navigateByUrl(appRoute.dashboard.baseRoot)
        })
      }

    })
  ), { dispatch: false })

  constructor(private store: Store, private actions$: Actions, private authService: ExtAuthService, private router: Router, private smartflowService: CxmSmartflowService, private translate: TranslateService, private confirmMessageService: ConfirmationMessageService, private depositService: FlowDepositService,
    private snackbar: SnackBarService) { }

}
