import { Observable } from 'rxjs';
import { ActivatedRouteSnapshot, CanDeactivate, RouterStateSnapshot } from '@angular/router';

export class CanDeactivateGuard implements CanDeactivate<any> {
  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot,
  ): boolean | Observable<boolean> | Promise<boolean> {
    localStorage.removeItem(`report-filtering-histories-Postal`);
    localStorage.removeItem(`report-filtering-histories-global`);
    localStorage.removeItem(`report-filtering-histories-SMS`);
    localStorage.removeItem(`report-filtering-histories-Email`);
    return true;
  }
}
