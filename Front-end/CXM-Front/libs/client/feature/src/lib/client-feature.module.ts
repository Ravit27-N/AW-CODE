import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ParameterClientComponent } from './parameter-client.component';

import { RouterModule, Routes } from '@angular/router';
import { ClientDataAccessModule } from '@cxm-smartflow/client/data-access';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';


const routes: Routes = [
  {
    path: '',
    component: ParameterClientComponent,
    children: [
      {
        path: 'list',
        loadChildren: () => import('@cxm-smartflow/client/ui/feature-client-list').then(m => m.ClientUiFeatureClientListModule)
      },
      {
        path: 'c',
        component: ParameterClientComponent,
        loadChildren: () => import('@cxm-smartflow/client/ui/feature-client-modification').then(m => m.ClientUiFeatureClientModificationModule),

      }
    ]
  }
]



@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ClientDataAccessModule,
    SharedTranslateModule.forRoot(),
  ],
  declarations: [
    ParameterClientComponent
  ],
})
export class ClientFeatureModule { }
