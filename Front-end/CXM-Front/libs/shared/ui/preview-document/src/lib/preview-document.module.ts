import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PreviewDocumentComponent } from './preview-document.component';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';
import { RouterModule } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@NgModule({
  imports: [
    CommonModule,
    NgxExtendedPdfViewerModule,
    MatProgressSpinnerModule,
    RouterModule.forChild([
      {
        path: '',
        component: PreviewDocumentComponent,
      },
    ]),
  ],
  declarations: [PreviewDocumentComponent],
  exports: [PreviewDocumentComponent],
  providers: [Title],
})
export class PreviewDocumentModule {}
