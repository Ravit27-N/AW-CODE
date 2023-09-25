import { CxmTemplateComponent } from './cxm-template/cxm-template.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    component: CxmTemplateComponent,
    children: [
      {
        path: '',
        redirectTo: 'design-model',
      },
      {
        path: 'design-model',
        loadChildren: () =>
          import('@cxm-smartflow/template/feature').then(
            (m) => m.EmailTemplateFeatureModule
          ),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CxmTemplateRoutingModule {}
