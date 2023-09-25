import {Injectable} from '@angular/core';
import { Router } from '@angular/router';
import { NullValidationHandler, OAuthErrorEvent, OAuthService } from 'angular-oauth2-oidc';
import { BehaviorSubject, combineLatest, from, Observable, ReplaySubject } from 'rxjs';
import { filter, first, map, mapTo } from 'rxjs/operators';
import { AppConfigService } from '../core';
import { parseJwt } from './jwt';
import {environment} from "../../environments/environment";

@Injectable({ providedIn: 'root' })
export class UserService {

  private isAuthenticatedSubject$ = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject$.asObservable();

  private isDoneLoadingSubject$ = new ReplaySubject<boolean>(1);
  public isDoneLoading$ = this.isDoneLoadingSubject$.asObservable();
  private isRolesLoadedSubject$ = new ReplaySubject<boolean>(1);
  public isRoleloaded$ = this.isRolesLoadedSubject$.asObservable();
  private appAccess$ = new ReplaySubject<any>(1);
  public userAccess$ = this.appAccess$.asObservable();

  public canActivateProctedRoutes$: Observable<boolean> = combineLatest([
    this.isAuthenticated$,
    this.isDoneLoading$,
    this.isRoleloaded$
  ]).pipe(map(values => values.every(b => b)));

  constructor(private auth: OAuthService, private appconfig: AppConfigService, private router: Router) {

    this.auth.events.subscribe(event => {
      if (event instanceof OAuthErrorEvent) {
        console.error('OAuthErrorEvent Object:', event);
      } else {
        console.warn('OAuthEvent Object:', event);
      }
    });

    this.isAuthenticatedSubject$.pipe(filter(v => v)).pipe(first()).subscribe(() => this.tryLoadConfig());

    this.auth.tokenValidationHandler = new NullValidationHandler();

    window.addEventListener('storage', (event) => {
      if (event.key !== 'access_token' && event.key != null) {
        return;
      }

      this.isAuthenticatedSubject$.next(this.auth.hasValidAccessToken());

      if (!this.auth.hasValidAccessToken()) {
        this.navigateToLoginPage();
      }
    });

    this.auth.events.subscribe(() => {
      this.isAuthenticatedSubject$.next(this.auth.hasValidAccessToken());
    });

    this.auth.events.pipe(filter(e => ['token_received'].includes(e.type)))
      .subscribe(() => {
        this.isAuthenticatedSubject$.next(true);
      });

    this.auth.events.pipe(filter(e => ['session_terminated', 'session_error'].includes(e.type)))
      .subscribe(() => this.navigateToLoginPage());
  }

  private navigateToLoginPage(): void {
    this.router.navigateByUrl(`${environment.basePath}welcome`);
  }

  private tryLoadConfig() {
    from(this.appconfig.tryLoad())
      .pipe(mapTo(true)).subscribe(ready => {
        const ROLES = this.appconfig.getRoleProjection(this.getCurrentRoles());
        this.appAccess$.next(ROLES);
        this.isRolesLoadedSubject$.next(ready);
      });

    this.auth.loadUserProfile();
  }

  runInitialLoginSequence(): Promise<void> {
    return this.auth.loadDiscoveryDocumentAndTryLogin()
      .then(() => {

        this.isDoneLoadingSubject$.next(true);

        if (this.auth.hasValidAccessToken()) {
          this.isAuthenticatedSubject$.next(true);
          return Promise.resolve();
        }

        return this.auth.silentRefresh()
          .then(() => { console.log('silentRefresh'); Promise.resolve();})
          .catch(result => {

            const errorResponsesRequiringUserInteraction = [
              'interaction_required',
              'login_required',
              'account_selection_required',
              'consent_required',
            ];

            if (result && result.reason && errorResponsesRequiringUserInteraction.indexOf(result.reason.error) > 0) {
              console.warn('User interaction is needed to log in, we will wait for the user to manually log in.');
              return Promise.resolve();
            }

            return Promise.reject(result);
          });
      })
      .then(() => {
        this.isDoneLoadingSubject$.next(true);

        // TODO: Check for the strings 'undefined' and 'null' just to be sure. Our current
      })
      .catch(() => this.isDoneLoadingSubject$.next(true));
  }

  login(targetUrl?: string): void {
    this.auth.initCodeFlow(targetUrl || this.router.url);
  }

  logout(): void {
    this.isAuthenticatedSubject$.next(false);
    this.auth.logOut();
  }

  refresh(): void {
    this.auth.silentRefresh();
  }

  hasValidToken(): boolean {
    return this.auth.hasValidAccessToken();
  }

  getCurrentUser(): ClaimUser {
    return this.auth.getIdentityClaims() as ClaimUser;
  }

  getCurrentRoles() {
    const jwt = parseJwt(this.auth.getAccessToken());

    let audience = jwt.aud as string;
    if (Array.isArray(jwt.aud)) {
      audience = jwt.aud[0];
    }

    return jwt.resource_access[audience].roles;
  }
}

export interface ClaimUser {
  // naming member have to match field in JWT token
  // eslint-disable-next-line @typescript-eslint/naming-convention
  preferred_username: string;
}
