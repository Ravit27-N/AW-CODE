import { IAppSettings } from '@cxm-smartflow/shared/app-config';
import { AuthConfig } from 'angular-oauth2-oidc';

export const authConfigPassword = (appSettings: IAppSettings): AuthConfig => {

  return {
    // Url of the Identity Provider
    issuer: appSettings.keycloak,

    // URL of the SPA to redirect the user to after login
    // redirectUri: window.location.origin + '/index.html',

    // The SPA's id. The SPA is registered with this id at the auth-server
    clientId: appSettings.clientId,

    // Login-Url
    // tokenEndpoint: appSettings.tokenEndpoint,

    // Url with user info endpoint
    // This endpont is described by OIDC and provides data about the loggin user
    // This sample uses it, because we don't get an id_token when we use the password flow
    // If you don't want this lib to fetch data about the user (e. g. id, name, email) you can skip this line
    // userinfoEndpoint: appSettings.userinfoEndpoint,

    // Just needed if your auth server demands a secret. In general, this
    // is a sign that the auth server is not configured with SPAs in mind
    // and it might not enforce further best practices vital for security
    // such applications.
    dummyClientSecret: appSettings.dummyClientSecret,
    // dummyClientSecret: 'secret',

    responseType: 'code',

    // set the scope for the permissions the client should request
    // The first four are defined by OIDC.
    // Important: Request offline_access to get a refresh token
    // The api scope is a usecase specific one
    scope: 'openid email',
    showDebugInformation: true,
    requireHttps: false,
    // timeoutFactor: 0.05,
    oidc: false
  }
};
