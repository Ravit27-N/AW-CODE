import { TranslateService } from '@ngx-translate/core';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, exhaustMap, map, switchMap, tap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import * as fromAuthAction from './auth.actions';

@Injectable()
export class AuthEffects {
  messageProps: any;
  constructor(
    private translate: TranslateService,
    private actions$: Actions,
    private authService: AuthService,
    private router: Router,
    private snackBar: SnackBarService,
    private activatedRoute: ActivatedRoute
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
    this.translate
      .get('login')
      .subscribe((res) => (this.messageProps = res));
  }

  loadAuthLogin = createEffect(() =>
    this.actions$.pipe(
      ofType(fromAuthAction.loadAuthLogin),
      exhaustMap(
        (arg) =>
        this.authService.login(arg.loginForm).then((response) => {
          if (response.error) {
            return fromAuthAction.validateUserCredential({loginForm: arg.loginForm});
          } else {
            if(response.isBlocked) {
              return fromAuthAction.checkNotBlockedAccount({loginForm: arg.loginForm});
            }
            localStorage.setItem('user', response?.preferred_username);
            return fromAuthAction.loadAuthSuccess({ keycloakResponse: response });
          }
        })
      )
    )
  );

  loadAuthLoginSuccess = createEffect(
    () =>
      this.actions$.pipe(
        ofType(fromAuthAction.loadAuthSuccess),
        switchMap((args) => [
          fromAuthAction.loadUserProfile({ name: args.keycloakResponse.name || '', forceToChangePassword: args.keycloakResponse.forceToChangePassword || false })])
      )
  );

  validateUserCredential = createEffect(() => this.actions$.pipe(
    ofType(fromAuthAction.validateUserCredential),
    tap(() => {
      this.translate.get('login.form.username.notexisted').toPromise().then(msg => this.snackBar.openCustomSnackbar({message: msg, type: 'error', icon: 'close'}));
    })
  ), { dispatch: false });

  checkNotBlockedAccount  = createEffect(() => this.actions$.pipe(
    ofType(fromAuthAction.checkNotBlockedAccount),
    tap(() => {
      this.translate.get('login.form.username.notexisted').toPromise().then(msg => this.snackBar.openCustomSnackbar({message: msg, type: 'error', icon: 'close'}));
    })
  ), { dispatch: false });

  validateUserCredentialFail = createEffect(() => this.actions$.pipe(
    ofType(fromAuthAction.validateUserCredentialFail),
    tap(() => {
      this.snackBar.openCustomSnackbar({message: this.messageProps?.loadProfileFail, type: 'error', icon: "close"});
    })
  ), {dispatch: false});

  loadUserProfile = createEffect(
    () => this.actions$.pipe(
      ofType(fromAuthAction.loadUserProfile),
      switchMap((args) => this.authService.getUserPrivileges(args.forceToChangePassword)
      .pipe()
        .pipe(map(
            //(response) => fromAuthAction.loadUserProfileSuccess(response)
            (response) => args.forceToChangePassword ? fromAuthAction.loadUserForceToChangePassword(response) :
            fromAuthAction.loadUserProfileSuccess(response)
          ),
          catchError(async (error) => fromAuthAction.loadUserProfileFail({ error: error }))
        )
      )
    )
  );

  loadUserProfileSuccess = createEffect(
    () => this.actions$.pipe(
      ofType(fromAuthAction.loadUserProfileSuccess),
      tap((args) => {
          this.authService.setUserPrivilegesToStorage(JSON.stringify(args))
            .subscribe(() => {
              const { returnTo } = this.activatedRoute.snapshot.queryParams;
              if(returnTo) {
                this.router.navigateByUrl(returnTo);
              } else {
                this.router.navigateByUrl('dashboard');
              }
            });
        }
      )
    ),
    { dispatch: false }
  );

  loadUserForceToChangePassword = createEffect(
    () => this.actions$.pipe(
      ofType(fromAuthAction.loadUserForceToChangePassword),
      tap((args) => {
          this.authService.setUserPrivilegesToStorage(JSON.stringify(args))
            .subscribe(() => {
              const { returnTo } = this.activatedRoute.snapshot.queryParams;
              if(returnTo) {
                this.router.navigateByUrl(returnTo);
              } else {
                this.router.navigateByUrl('blocked-account');
              }
            });
        }
      )
    ),
    { dispatch: false }
  );

  loadUserProfileFail = createEffect(
    () => this.actions$.pipe(
      ofType(fromAuthAction.loadUserProfileFail),
      tap(() => {
        this.showLoadProfileFail();
        this.authService.removeUserPrivilegesFromStorage();
      })
    ),
    {dispatch: false}
  )

  logout = createEffect(
    () =>
      this.actions$.pipe(
        ofType(fromAuthAction.logout),
        tap(() => {
          this.authService.logout();
        })
      ),
    { dispatch: false }
  );

  private showLoadProfileFail(){
    this.snackBar.openCustomSnackbar({message: this.messageProps?.loadProfileFail, type: 'error', icon: 'close'});
  }

}
