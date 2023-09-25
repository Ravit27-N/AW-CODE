import { loadRemoteModule, LoadRemoteModuleOptions } from '@angular-architects/module-federation';
import { Route, Routes } from '@angular/router';
import { DashboardComponent } from '../app/dashboard/dashboard.component';

export type Microfrontend = LoadRemoteModuleOptions & {
  displayName: string;
  routePath: string;
  ngModuleName: string;
}

export function buildRemoteRouting(apps: Microfrontend[], version?: any): Route[] {
  const { buildVersion } = version;

  const lazyRoutes: Routes = apps
  .map(a => ({
    ...a, exposedModule: './Module',
  }))
  .map(a => {
    if(!version) return a;

    return { ...a, remoteEntry: `${a.remoteEntry}?v=${buildVersion}` }
  })
  .map(o => ({
    path: o.routePath,
    loadChildren: () => loadRemoteModule(o).then(m => m[o.ngModuleName])
}));

  if(apps.length > 0) {

    const arr = [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard',
      },
      {
        path: 'dashboard',
        component: DashboardComponent,
      },
    ];

    lazyRoutes.push(...arr);
  }

  return lazyRoutes
}
