import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Route, RouterModule } from '@angular/router';
import { ProductionCriteriaComponent } from './production-criteria.component';
import { FlowDepositUiIdentificationTableModule } from '@cxm-smartflow/flow-deposit/ui/identification-table';
import { FlowDepositUiProductCriteriaFormModule } from '@cxm-smartflow/flow-deposit/ui/product-criteria-form';
import { FlowDepositUiDepositNavigatorModule } from '@cxm-smartflow/flow-deposit/ui/deposit-navigator';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FlowDepositUiPdfViewerModule } from '@cxm-smartflow/flow-deposit/ui/pdf-viewer';
import { FlowDepositUiHeaderModule } from '@cxm-smartflow/flow-deposit/ui/header';
import { FlowDepositUiFileInfoModule } from '@cxm-smartflow/flow-deposit/ui/file-info';
import { LockableFormGuardService } from '@cxm-smartflow/flow-deposit/guard/pending-change';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

export const flowDepositUiProductionCriteriaRoutes: Route[] = [
  {
    path: '',
    component: ProductionCriteriaComponent,
    canDeactivate: [LockableFormGuardService],
  }
];

@NgModule({
  imports: [
    CommonModule,
    SharedTranslateModule.forRoot(),
    FlowDepositUiIdentificationTableModule,
    FlowDepositUiProductCriteriaFormModule,
    FlowDepositUiDepositNavigatorModule,
    FlowDepositUiPdfViewerModule,
    MaterialModule,
    FlowDepositUiHeaderModule,
    RouterModule.forChild(flowDepositUiProductionCriteriaRoutes),
    FlowDepositUiFileInfoModule
  ],
  exports: [RouterModule],
  declarations: [
    ProductionCriteriaComponent
  ],
})
export class FlowDepositUiProductionCriteriaModule { }
