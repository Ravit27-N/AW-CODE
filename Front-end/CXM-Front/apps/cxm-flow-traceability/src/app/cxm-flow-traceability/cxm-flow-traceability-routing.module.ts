/* eslint-disable @nrwl/nx/enforce-module-boundaries */
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CxmFlowTraceabilityComponent } from './cxm-flow-traceability.component';

const routes: Routes = [
  {
    path: '',
    component: CxmFlowTraceabilityComponent,
   children: [
     {
       path: '',
       loadChildren: () =>
       import('@cxm-smartflow/flow-traceability/feature').then(
        (m) => m.FlowTraceabilityFeatureModule
       )
     },
     {
      path: 'espace',
      loadChildren: () => import('@cxm-smartflow/approval/feature').then(m => m.ApprovalFeatureModule)
     }
   ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CxmFlowTraceabilityRoutingModule { }
