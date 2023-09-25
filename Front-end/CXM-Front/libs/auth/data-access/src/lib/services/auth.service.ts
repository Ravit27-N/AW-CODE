import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { IAppSettings } from '@cxm-smartflow/shared/app-config';
import { ConfigurationService } from '@cxm-smartflow/shared/data-access/api';
import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import {
  appLocalStorageConstant,
  AuthenticationConstant,
  keyCloakModel,
  LoginModel,
  UserInfoModel,
  UserInFoModelForm,
  UserPrivilegeModel,
  AuthenticationAttemptsRequest,
  AuthenticationAttempts,
  UserLoginAttempt,
  UserInfoAndAuthorizedToAuthenticate
} from '@cxm-smartflow/shared/data-access/model';
import { cxmProfileEnv } from '@env-cxm-profile';
import { OAuthService, UserInfo } from 'angular-oauth2-oidc';
import { Observable, of, throwError } from 'rxjs';
import {catchError, filter} from 'rxjs/operators';
import { UserCredentialModel } from '../models';
import { authConfigPassword } from './oauth.config';

@Injectable()
export class AuthService {

  public static USER_PRIVILEGES = 'userPrivileges';

  settings: IAppSettings;

  constructor(
    private oauthService: OAuthService,
    private http: HttpClient,
    private router: Router
  ) {
    const configuration = new ConfigurationService();
    const s = configuration.getAppSettings();
    oauthService.configure(authConfigPassword(s));
    oauthService.setStorage(localStorage);
    oauthService.loadDiscoveryDocument().then(() => {
      oauthService.setupAutomaticSilentRefresh();
      this.oauthService.events
        .pipe(filter((element) => element.type === 'token_refresh_error'))
        .subscribe(() => {
          this.logout();
        });
    });

    this.settings = s;
  }

  get headers(): HttpHeaders {
    const headersConfig = {
      'Content-Type': 'application/json',
      Accept: 'application/json'
    };
    return new HttpHeaders(headersConfig);
  }

  private get<T>(url: string, path: string, params: HttpParams = new HttpParams()): Observable<T> {
    return this.http.get<T>(`${url}${path}`, {
      headers: this.headers,
      params
    }).pipe(catchError(e => throwError(e)));
  }

  post<T, D>(path: string, data: D, params: HttpParams = new HttpParams()): Observable<T> {
    return this.http.post<T>(`${this.settings.apiGateway}${path}`, JSON.stringify(data), { headers: this.headers, params })
      .pipe(catchError(e => throwError(e)));
  }

  async login(loginForm: LoginModel): Promise<any> {
    
    const userLoginAttemptRequest = <UserLoginAttempt>{
      userName : loginForm.username,
      loginStatus: false
    };

     let userInfoAndAuthorizedToAuthenticate = <UserInfoAndAuthorizedToAuthenticate>{};

    return this.checkNotBlockedAccountAsync(loginForm)
    .then(async (response) => {
      if(response.isBlocked) {
        return response;
      } else {

        const forceToChangePassword: boolean = response.forceToChangePassword;

        return this.oauthService
      .fetchTokenUsingPasswordFlowAndLoadUserProfile(loginForm.username, loginForm.password)
      .then(async (response) => {

        userLoginAttemptRequest.loginStatus = forceToChangePassword;
        userLoginAttemptRequest.password = loginForm.password;
        this.postUserLoginAttempt(userLoginAttemptRequest).subscribe();

        userInfoAndAuthorizedToAuthenticate = response;
        userInfoAndAuthorizedToAuthenticate.forceToChangePassword = forceToChangePassword;
        return userInfoAndAuthorizedToAuthenticate;
      })
      .catch((err) => {
        userLoginAttemptRequest.loginStatus = true;
        this.postUserLoginAttempt(userLoginAttemptRequest).subscribe();
        return err;
      });

      }
    })
    .catch((err) => {
      return err;
    });
    
  }

  public getIdentityClaims(): keyCloakModel {
    return this.oauthService.getIdentityClaims();
  }

  public async loadUserProfile() {
    return this.oauthService.loadUserProfile();
  }

  public access_token() {
    return this.oauthService.getAccessToken();
  }

  public access_token_expiration() {
    return this.oauthService.getAccessTokenExpiration();
  }

  public logout() {
    this.oauthService.logOut();
    window.location.reload(); // Need to be reload, clear all catch & destroyed (angular objects).
  }

  public removeUserPrivilegesFromStorage() {
    const locale = localStorage.getItem('locale') || 'fr';
    localStorage.clear();
    localStorage.setItem('locale', locale);
    localStorage.removeItem(AuthenticationConstant.USER_PRIVILEGES);
  }

  public getUserPrivileges(forceToChangePassword = false): Observable<any> {
    const params = new HttpParams().set("forceToChangePassword",forceToChangePassword);
    return this.get(this.settings.apiGateway, cxmProfileEnv.profileContext + '/profiles/user-privileges', params);
  }

  public clearHeader(): void {
    const locale = localStorage.getItem('locale') || 'fr';
    localStorage.clear();
    localStorage.setItem('locale', locale);
    this.oauthService.logOut(true);
  }

  public setUserPrivilegesToStorage(value?: any): Observable<boolean> {
    localStorage.setItem(AuthenticationConstant.USER_PRIVILEGES, value);
    return of(true);
  }

  public validateUserCredential(payload: LoginModel): Observable<UserCredentialModel> {
    return this.post(cxmProfileEnv.profilePublicContext + '/users/validate-user-credential', payload);
  }

  public isAdminUser(): boolean {
    return JSON.parse(<string>localStorage.getItem(appLocalStorageConstant.UserManagement.UserPrivilege || ''))?.admin;
  }

  public getUserInfoByToken(): Observable<UserInfoModel> {
    return this.get(this.settings.apiGateway, cxmProfileEnv.profileContext + '/users/user-info');
  }

  public updateUserPassword(userInfo: UserInFoModelForm): Observable<any>{
    return this.post(cxmProfileEnv.profileContext + '/users/update-user-password', userInfo);
  }

  private checkNotBlockedAccount(payload: LoginModel): Observable<AuthenticationAttempts> {
    const request = <AuthenticationAttemptsRequest>{
      userName : payload.username
     };
    return this.post(cxmProfileEnv.profilePublicContext + '/users/login-attempts', request);
  }

  public postUserLoginAttempt(payload: UserLoginAttempt): Observable<UserLoginAttempt> {
    return this.post(cxmProfileEnv.profilePublicContext + '/users/login-attempts/add', payload);
  }

  public async postUserLoginAttemptAsync(payload: UserLoginAttempt): Promise<UserLoginAttempt> {
    return await this.postUserLoginAttempt(payload).toPromise();
  }

  public async checkNotBlockedAccountAsync(payload: LoginModel): Promise<AuthenticationAttempts> {
    return await this.checkNotBlockedAccount(payload).toPromise();
  }

}
