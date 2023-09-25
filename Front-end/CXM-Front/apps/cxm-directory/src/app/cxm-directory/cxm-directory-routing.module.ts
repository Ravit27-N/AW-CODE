import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CxmDirectoryComponent } from './cxm-directory.component';

const routes: Routes = [
  {
    path: '',
    component: CxmDirectoryComponent,
    children: [
      {
        path: '',
        loadChildren: () =>
          import('@cxm-smartflow/definition-directory/feature').then(
            (m) => m.DirectoryDefinitionFeatureModule
          )
      },
      {
        path: 'directory-feed',
        loadChildren: () =>
          import('@cxm-smartflow/directory-feed/feature').then(
            (m) => m.DirectoryFeedFeatureModule
          )
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CxmDirectoryRoutingModule {
}
