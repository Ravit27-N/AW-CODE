import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CxmAnalyticsComponent } from './cxm-analytics.component';

const routes: Routes = [
  {
    path: '',
    component: CxmAnalyticsComponent,
    children: [
      {
        path: '',
        loadChildren: () =>
          import('@cxm-smartflow/analytics/feature').then(
            (m) => m.AnalyticsFeatureModule
          ),
      },
      {
        path: 'dashboard',
        loadChildren: () => import('@cxm-smartflow/dashboard/feature').then(m => m.DashboardFeatureModule),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CxmAnalyticsRoutingModule {}
