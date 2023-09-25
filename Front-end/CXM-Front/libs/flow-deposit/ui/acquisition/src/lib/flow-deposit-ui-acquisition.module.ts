import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Route, RouterModule } from '@angular/router';
import { FeatureAcquisitionComponent } from './feature-acquisition.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FlowDepositUiUploadProgressionModule } from '@cxm-smartflow/flow-deposit/ui/upload-progression';
import { SharedDirectivesDragDropModule } from '@cxm-smartflow/shared/directives/drag-drop';
import { FlowDepositDataAccessModule } from '@cxm-smartflow/flow-deposit/data-access';
import { FlowDepositUiDepositNavigatorModule } from '@cxm-smartflow/flow-deposit/ui/deposit-navigator';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { FlowDepositUiHeaderModule } from '@cxm-smartflow/flow-deposit/ui/header';
import { SharedUiProgressionLineModule } from '@cxm-smartflow/shared/ui/progression-line';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

export const flowDepositUiAcquisitionRoutes: Route[] = [
  {
    path: '',
    pathMatch: 'full',
    component: FeatureAcquisitionComponent
  }
];

@NgModule({
  imports: [CommonModule,
    RouterModule.forChild(flowDepositUiAcquisitionRoutes),
    SharedTranslateModule.forRoot(), MaterialModule,
    FlowDepositUiUploadProgressionModule,
    SharedDirectivesDragDropModule,
    FlowDepositDataAccessModule,
    FlowDepositUiDepositNavigatorModule,
    SharedUiSpinnerModule,
    FlowDepositUiHeaderModule,
    SharedUiProgressionLineModule
  ],
  exports: [RouterModule],
  declarations: [
    FeatureAcquisitionComponent
  ],
})
export class FlowDepositUiAcquisitionModule {}
