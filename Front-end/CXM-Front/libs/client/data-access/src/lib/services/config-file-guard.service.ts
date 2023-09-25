import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { UserUtil } from '@cxm-smartflow/shared/data-access/services';
import { appRoute } from '@cxm-smartflow/shared/data-access/model';

@Injectable({
  providedIn: 'root'
})
export class ConfigFileGuardService {
  constructor(private _router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const accessible = UserUtil.isAdmin();

    if (accessible) {
      return true;
    }

    this._router.navigateByUrl(appRoute.dashboard.baseRoot);
    return accessible;
  }
}
