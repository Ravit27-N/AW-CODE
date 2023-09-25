import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalysisResultComponent } from './analysis-result.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FlowDepositUiIdentificationTableModule } from '@cxm-smartflow/flow-deposit/ui/identification-table';
import { FlowDepositUiAnalysisVariantModule } from '@cxm-smartflow/flow-deposit/ui/analysis-variant';
import { FlowDepositUiAnalysisResultTableModule } from '@cxm-smartflow/flow-deposit/ui/analysis-result-table';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FlowDepositUiDepositNavigatorModule } from '@cxm-smartflow/flow-deposit/ui/deposit-navigator';
import { FlowDepositUiPdfViewerModule } from '@cxm-smartflow/flow-deposit/ui/pdf-viewer';
import { FlowDepositUiHeaderModule } from '@cxm-smartflow/flow-deposit/ui/header';
import { FlowDepositUiFileInfoModule } from '@cxm-smartflow/flow-deposit/ui/file-info';
import { LockableFormGuardService } from '@cxm-smartflow/flow-deposit/guard/pending-change';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    SharedTranslateModule.forRoot(),
    FlowDepositUiIdentificationTableModule,
    FlowDepositUiAnalysisVariantModule,
    FlowDepositUiAnalysisResultTableModule,
    FlowDepositUiDepositNavigatorModule,
    FlowDepositUiPdfViewerModule,
    FlowDepositUiHeaderModule,
    FlowDepositUiFileInfoModule,
    RouterModule.forChild([
      {
        path: '',
        component: AnalysisResultComponent,
        canDeactivate: [LockableFormGuardService],
      }
    ]),
    SharedPipesModule
  ],
  declarations: [
    AnalysisResultComponent
  ],
})
export class FlowDepositUiAnalysisResultModule {}
