import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, exhaustMap, map, take, tap, withLatestFrom } from 'rxjs/operators';
import { ProfileService } from '@cxm-smartflow/profile/data-access';


import {
  errorForgotResult,
  forgotPasswordSuccess,
  initialForgotPassword,
  requesetResetPassword,
  requestChangePassword,
  requestResetPasswordFail,
  selectForgotState,
  validateToken,
  validateTokenFail,
  validateTokenSuccess
} from './forgot-password.store';
import { Store } from '@ngrx/store';
import { of } from 'rxjs';
import { Router } from '@angular/router';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';

@Injectable({ providedIn: 'root' })
export class ForgotPasswordEffect {


  requestChangePasswordEffect$ = createEffect(() => this.action.pipe(
    ofType(requestChangePassword),
    exhaustMap(args => {
      return this.service.requestForgotPassword(args.email).pipe(
        map(res => forgotPasswordSuccess({ successMode: 1 })),
        // catchError(err => of(errorForgotResult({ error: err }))),
        catchError(err => of(forgotPasswordSuccess({ successMode: 1 })))
      );
    })
  ));


  requestResetPasswordEffect$ = createEffect(() => this.action.pipe(
    ofType(requesetResetPassword),
    withLatestFrom(this.store.select(selectForgotState)),
    exhaustMap(([args, state]) => {
      const { token } = state as any;
      return this.service.requestChangePassword(args.password, token).pipe(
        map(res => forgotPasswordSuccess({ successMode: 2 })),
        catchError(httpErrorResponse => [requestResetPasswordFail({ httpErrorResponse })])
      );
    })
  ));

  requestResetPasswordFail$ = createEffect(() => this.action.pipe(
    ofType(requestResetPasswordFail),
    tap(() => {
      this.router.navigateByUrl('/forgot-password/expire-link');
    })
  ), {dispatch: false})


  // errorForgotResultEffect$ = createEffect(() => this.action.pipe(
  //   ofType(errorForgotResult),
  //   tap(({ error }) => {
  //     if(error.status === 404) {
  //       // this.translateService.get('forgot-password.emailNotFound').pipe(take(1))
  //       //   .subscribe(v => this.snackbarService.openError(v));
  //     }
  //   })
  // ), { dispatch: false });

  validateToken$ = createEffect(() => this.action.pipe(
    ofType(validateToken),
    exhaustMap(({ token }) => this.service.validateToken(token).pipe(
      map(isExpired => validateTokenSuccess({ isExpired, token })),
      catchError(httpErrorResponse => [validateTokenFail({ httpErrorResponse })])
    ))
  ));

  validateTokenSuccess$ = createEffect(() => this.action.pipe(
    ofType(validateTokenSuccess),
    tap(({ isExpired, token }) => {
      if (!isExpired) {
        const params = { token, nonExpired: true };
        this.router.navigate(['/forgot-password'], { queryParams: params });
        this.store.dispatch(initialForgotPassword({ mode: 1, token, nonExpired: true }));
      } else {
        this.router.navigateByUrl('/forgot-password/expire-link');
      }
    })
  ), { dispatch: false });

  validateTokenFail$ = createEffect(() => this.action.pipe(
    ofType(validateTokenFail),
    tap(({ httpErrorResponse }) => {
      this.router.navigateByUrl('/forgot-password/expire-link');
    })
  ), { dispatch: false });

  constructor(private action: Actions, private service: ProfileService, private store: Store, private router: Router,
              private snackbarService: SnackBarService, private translateService: TranslateService) {
    this.translateService.use(localStorage.getItem('locale') || 'fr');
  }

}
