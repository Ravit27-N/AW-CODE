import { AuthConfig } from 'angular-oauth2-oidc';
import { environment } from '../../environments/environment';


export const pkceflow: AuthConfig = {
  issuer: environment.oauth.issuer,
  clientId: environment.oauth.clientId,
  dummyClientSecret: environment.oauth.dummyClientSecret,
  redirectUri: window.location.origin + `${environment.basePath}welcome`,
  responseType: 'code',
  scope: 'openid profile offline_access',
  showDebugInformation: true,
  disableAtHashCheck: true,
  requireHttps: false,
};

