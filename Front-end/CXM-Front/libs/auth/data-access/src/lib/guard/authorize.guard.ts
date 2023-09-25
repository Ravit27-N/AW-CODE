import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { OAuthService } from 'angular-oauth2-oidc';
import { Location } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';

@Injectable({
  providedIn: 'any'
})
export class AuthorizeGuard implements CanActivate {

  /**
   * Constructor
   */
  constructor(private _oauthService: OAuthService, private _location: Location, private _router: Router, private _dialogRef: MatDialog,) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this._oauthService.hasValidAccessToken()) {
      // Clears browser history, so they can't navigate with back button.
      this._location.replaceState('/cxm-analytics/dashboards');
      return true;
    } else {
      // Clears browser history, so they can't navigate with back button.
      location.replace(document.baseURI);
      return false;
    }
  }

}
