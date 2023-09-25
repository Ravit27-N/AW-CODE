import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UploadingFragmentComponent } from './uploading-fragment.component';
import { TranslateModule } from '@ngx-translate/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { SharedDirectivesDragDropModule } from '@cxm-smartflow/shared/directives/drag-drop';

@NgModule({
  imports: [
    CommonModule,
    TranslateModule,
    ReactiveFormsModule,
    MatIconModule,
    SharedDirectivesDragDropModule,
  ],
  declarations: [UploadingFragmentComponent],
  exports: [UploadingFragmentComponent],
})
export class SharedUiUploadingFragmentModule {}
