import { Location } from '@angular/common';
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(private router: Router, private oauthService: OAuthService, private location: Location) {
  }

  canActivate() {
    if (this.oauthService.hasValidAccessToken()) {
      this.location.replaceState('/cxm-analytics/dashboards'); // clears browser history so they can't navigate with back button
      return true;
    } else {
      this.location.replaceState('/cxm-analytics/dashboards'); // clears browser history so they can't navigate with back button
      this.router.navigate(['/login']);
      return false;
    }
  }

}
