import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Store } from '@ngrx/store';
import { withLatestFrom } from 'rxjs/operators';
import { initialForgotPassword, selectForgotState, validateToken } from './store/forgot-password.store';


@Injectable({  providedIn: 'root' })
export class ForgotPasswordQueryResolver implements Resolve<any> {


  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const { token, nonExpired } = route.queryParams;

    if (!nonExpired && token) {
      this.store.dispatch(validateToken({ token }));
    }

    if (token) {
      this.store.dispatch(initialForgotPassword({ mode: 1, token, nonExpired: Boolean(nonExpired) }))
    } else {
      this.store.dispatch(initialForgotPassword({ mode: 0 }));
    }

    return withLatestFrom(this.store.select(selectForgotState));
  }

  constructor(private store: Store) { }
}
