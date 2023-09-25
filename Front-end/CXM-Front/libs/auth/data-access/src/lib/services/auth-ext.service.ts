import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { ConfigurationService } from "@cxm-smartflow/shared/data-access/api";
import { OAuthService } from "angular-oauth2-oidc";
import { AuthService } from "./auth.service";



@Injectable()
export class ExtAuthService extends AuthService {

  async loginWithToken(options: {
    access_token: string,
    refresh_token: string,
    expireAt: number
  }) {

    this.extCleanAuthState();
    this.extSaveToken(options.access_token, options.refresh_token, options.expireAt);

    // this.store.dispatch(loadImpersonateProfile());

    return new Promise((resolve, reject) => {
      resolve('done');
    })
  }

  clearReturn() {
    localStorage.removeItem('return_criteria');
  }

  protected extSaveToken(token: string, refresh_token: string, expireAt: number) {
    localStorage.setItem('access_token', token);
    localStorage.setItem('granted_scopes', JSON.stringify(['openid email profile']));
    localStorage.setItem('access_token_stored_at','' + Date.now());
    localStorage.setItem('expires_at', '' + this.getExpiredAt(expireAt));
    localStorage.setItem('refresh_token', refresh_token);
  }

  protected extCleanAuthState() {
    localStorage.clear();
  }

  protected getExpiredAt(expiresIn: number) {
    const expiresInMilliSeconds = expiresIn * 1000;
    const now = new Date().getTime();
    const expiresAt = now + expiresInMilliSeconds;

    return expiresAt;
  }

  constructor(
    oauthService: OAuthService,
    http: HttpClient,
    router: Router,
    // private store: Store
  ) {
    super(oauthService, http, router);
  }
}
