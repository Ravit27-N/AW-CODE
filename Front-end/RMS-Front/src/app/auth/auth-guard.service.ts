import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, switchMap, tap } from 'rxjs/operators';
import { UserService } from './user.service';

@Injectable()
export class AuthGuardService implements CanActivate {
  constructor(
    private userService: UserService
  ) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.userService.isDoneLoading$.pipe(
      filter(isDone => isDone),
      switchMap( () => this.userService.isAuthenticated$),
      tap(isAuthenticated  => isAuthenticated || this.userService.login(state.url))
    );
  }
}
