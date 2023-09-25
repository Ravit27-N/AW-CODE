import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { Observable } from 'rxjs';

@Injectable()
export class LoginGuard implements CanActivate {

  constructor(private router: Router, private oauthService: OAuthService) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    if (this.oauthService.hasValidAccessToken() && Boolean(localStorage.getItem('userPrivileges'))) {
      this.router.navigate(['/cxm-analytics/dashboards']);
      return false;
    } else {
      return true;
    }
  }

  isLogged() {
    return this.oauthService.hasValidAccessToken() && Boolean(localStorage.getItem('userPrivileges'));
  }

}
