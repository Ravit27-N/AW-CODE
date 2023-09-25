import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FinishedComponent } from './finished.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FlowDepositUiIdentificationTableModule } from '@cxm-smartflow/flow-deposit/ui/identification-table';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FlowDepositUiDepositNavigatorModule } from '@cxm-smartflow/flow-deposit/ui/deposit-navigator';
import { FlowDepositUiPdfViewerModule } from '@cxm-smartflow/flow-deposit/ui/pdf-viewer';
import { FlowDepositDataAccessModule } from '@cxm-smartflow/flow-deposit/data-access';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { FlowDepositUiHeaderModule } from '@cxm-smartflow/flow-deposit/ui/header';
import { FlowDepositUiFileInfoModule } from '@cxm-smartflow/flow-deposit/ui/file-info';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { LockableFormGuardService } from '@cxm-smartflow/flow-deposit/guard/pending-change';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { BackgroundListComponent } from './background-list/background-list.component';
import { AttachmentListComponent } from './attachment-list/attachment-list.component'
import { SignatureListComponent } from './signature-list/signature-list.component';
import { WatermarkListComponent } from './watermark-list/watermark-list.component'



@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SharedTranslateModule.forRoot(),
    FlowDepositUiIdentificationTableModule,
    MaterialModule,
    FlowDepositUiPdfViewerModule,
    FlowDepositUiDepositNavigatorModule,
    FlowDepositDataAccessModule,
    SharedPipesModule,
    FlowDepositUiHeaderModule,
    FlowDepositUiFileInfoModule,
    SharedUiButtonModule,
    RouterModule.forChild([
      {
        path: '',
        component: FinishedComponent,
        canDeactivate: [LockableFormGuardService],
      }
    ])
  ],
  declarations: [
    FinishedComponent,
    BackgroundListComponent,
    AttachmentListComponent,
    SignatureListComponent,
    WatermarkListComponent
  ],
  exports: [
    FinishedComponent
  ]
})
export class FlowDepositUiFinishedModule {}
