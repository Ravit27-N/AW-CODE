import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UploadProgressionComponent } from './upload-progression.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';

import { FlowDepositDataAccessModule } from '@cxm-smartflow/flow-deposit/data-access';
import { SharedUiProgressionLineModule } from '@cxm-smartflow/shared/ui/progression-line';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedTranslateModule.forRoot(),
    MaterialModule,
    FlowDepositDataAccessModule,
    SharedUiProgressionLineModule,
  ],
  declarations: [UploadProgressionComponent],
  exports: [UploadProgressionComponent],
})
export class FlowDepositUiUploadProgressionModule {}
