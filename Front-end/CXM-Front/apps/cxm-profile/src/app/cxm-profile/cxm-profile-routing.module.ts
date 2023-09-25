import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CxmProfileComponent } from './cxm-profile.component';

const routes: Routes = [
  {
    path: '',
    component: CxmProfileComponent,
    children: [
      {
        path: '',
        loadChildren: () => import('@cxm-smartflow/profile/feature').then(
          (m) => m.ProfileFeatureModule
        )
      },
      {
        path: 'users',
        loadChildren: () => import('@cxm-smartflow/user/feature').then(
          (m) => m.UserFeatureModule
        )
      },
      {
        path: 'client',
        loadChildren: () => import('@cxm-smartflow/client/feature').then(m => m.ClientFeatureModule)
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CxmProfileRoutingModule {
}
