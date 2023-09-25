import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CxmSettingComponent } from './cxm-setting.component';

const routes: Routes = [
  {
    path: '',
    component: CxmSettingComponent,
    children: [
      {
        path: '',
        loadChildren: () =>
          import('@cxm-smartflow/setting/feature').then(
            (m) => m.SettingFeatureModule
          ),
      },
      {
        path: 'envelope-references',
        loadChildren: () =>
          import('@cxm-smartflow/envelope-reference/feature').then(
             (m) => m.EnvelopeReferenceFeatureModule
          ),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CxmSettingRoutingModule {}
