import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PreviewImageComponent } from './preview-image.component';
import { Title } from '@angular/platform-browser';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    CommonModule,
    MatProgressSpinnerModule,
    RouterModule.forChild([
      {
        path: '',
        component: PreviewImageComponent
      }
    ])
  ],
  declarations: [
    PreviewImageComponent
  ],
  exports: [PreviewImageComponent],
  providers: [Title]
})
export class SharedUiPreviewImageModule {
}
