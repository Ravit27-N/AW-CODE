import { InjectionToken } from "@angular/core";

declare global {
  interface Window {
    SMARTFLOWCONFIG: any
  }
}

const renameBaseUrl = (configuration: { contextPath: string }) => {
  if(!configuration.contextPath) return;

  const base = document.getElementsByTagName('base');
  if(base.length <= 0) {
    throw Error("Cannot config context path. base element not found")
  }

  base[0].href = configuration.contextPath;
}


export interface IAppSettings {
  version: number;

  apiGateway: string;

  keycloak: string;

  clientId: string;

  dummyClientSecret: string;

  apps: []
}


declare global {
  // eslint-disable-next-line no-var
  var __CXM__APP_SETTINGS: IAppSettings;
}

class RemoteAppsSettings implements IAppSettings {

  version: number;
  apiGateway: string;
  keycloak: string;
  clientId: string;
  dummyClientSecret: string;
  apps: []

  constructor(res: any) {
    const { version, apiGateway, keycloak, clientId, dummyClientSecret, apps } = res;

    this.version = version;
    this.apiGateway = apiGateway;
    this.keycloak = keycloak;
    this.clientId = clientId;
    this.dummyClientSecret = dummyClientSecret;
    this.apps = apps;
  }
}

export const appSettingProviderLoaderFactory = (configuration: any) => {
  localStorage.setItem('__CXM_APP_SETTINGS', JSON.stringify(configuration)); // store in local storage
  window.SMARTFLOWCONFIG = configuration; // store in window object

  // change context path
  // renameBaseUrl(configuration);

  return appSettingsProviderFactory();
}

export const appSettingsProviderFactory = () => {
  // const settings = window.__CXM__APP_SETTINGS;
  const config = window.SMARTFLOWCONFIG ?? JSON.parse(localStorage.getItem('__CXM_APP_SETTINGS') || '{}');
  return new RemoteAppsSettings(config);
  // return settings;
}

export const APP_SETTINGS = new InjectionToken<IAppSettings>('APP_SETTINGS');

export const appSetingProvider = {
  provide: APP_SETTINGS,
  useFactory: appSettingsProviderFactory,
  // multi: true
}

export const checkVersion = (localVersion: any, globalVersion: any) => {

  if(localVersion.build !== globalVersion.build) {
    caches.keys().then(function(names) {
      for (const name of names) caches.delete(name);
    });
    console.log('version change detected');
    // implement attempt counter retry
    window.location.reload();
  }
}
