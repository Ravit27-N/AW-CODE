import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PdfViewerComponent } from './pdf-viewer.component';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    NgxExtendedPdfViewerModule,
    SharedTranslateModule.forRoot(),
    MaterialModule,
  ],
  declarations: [PdfViewerComponent],
  exports: [PdfViewerComponent],
})
export class FlowDepositUiPdfViewerModule {}
