import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PreAnalysisComponent } from './pre-analysis.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FlowDepositUiIdentificationTableModule } from '@cxm-smartflow/flow-deposit/ui/identification-table';
import { HttpClientModule } from '@angular/common/http';
import { FlowDepositUiDepositNavigatorModule } from '@cxm-smartflow/flow-deposit/ui/deposit-navigator';
import { FlowDepositUiPdfViewerModule } from '@cxm-smartflow/flow-deposit/ui/pdf-viewer';
import { FlowDepositUiHeaderModule } from '@cxm-smartflow/flow-deposit/ui/header';
import { LockableFormGuardService } from '@cxm-smartflow/flow-deposit/guard/pending-change';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    SharedTranslateModule.forRoot(),
    MaterialModule,
    FlowDepositUiIdentificationTableModule,
    FlowDepositUiDepositNavigatorModule,
    FlowDepositUiPdfViewerModule,
    FlowDepositUiHeaderModule,
    RouterModule.forChild([
      {
        path: '',
        component: PreAnalysisComponent,
        canDeactivate: [LockableFormGuardService],
      }
    ])
  ],
  declarations: [
    PreAnalysisComponent
  ],
  exports: [
    PreAnalysisComponent
  ]
})
export class FlowDepositUiPreAnalysisModule {}
