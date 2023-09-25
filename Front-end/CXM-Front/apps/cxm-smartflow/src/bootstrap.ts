import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { appSettingProviderLoaderFactory, appSettingsProviderFactory, APP_SETTINGS, checkVersion } from '@cxm-smartflow/shared/app-config';

import { AppModule } from './app/app.module';
import { environment } from './environments/environment';
import * as localVersion from './build-version.json';

if (environment.production) {
  enableProdMode();
}


Promise.all([
  fetch(environment.remoteURL, {
    headers: {
      "Cache-Control": "no-cache, max-age=604800",
    }
  }).then(response => response.json()),
  fetch('build-version.json', {
    headers: {
      "Cache-Control": "no-cache, max-age=604800",
    }
  }).then(response => response.json())
])
.then(([configuration, globalVersion]) => {

  checkVersion(localVersion, globalVersion);
  appSettingProviderLoaderFactory(configuration);
  platformBrowserDynamic([
    {
      provide: APP_SETTINGS,
      useFactory: () => appSettingsProviderFactory()
    }
  ])
  .bootstrapModule(AppModule)
  .catch((err) => console.error(err));
})


