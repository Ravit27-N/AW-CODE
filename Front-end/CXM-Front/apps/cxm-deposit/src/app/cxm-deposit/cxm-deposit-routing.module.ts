import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CxmDepositComponent } from './cxm-deposit.component';

const routes: Routes = [{
  path: '',
  component: CxmDepositComponent,
  children: [
    {
      path: '',
      loadChildren: () => import('@cxm-smartflow/flow-deposit/feature').then(
        (m) => m.FlowDepositFeatureModule
      )
    },
    {
      path: 'communication-interactive',
      loadChildren: () => import('@cxm-smartflow/communication-interactive/feature').then(m => m.CommunicationInteractiveFeatureModule)
    }
  ]
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CxmDepositRoutingModule { }
