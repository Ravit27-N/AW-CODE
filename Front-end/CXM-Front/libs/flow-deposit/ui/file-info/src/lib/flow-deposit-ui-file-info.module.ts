import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FileInfoComponent } from './file-info.component';

@NgModule({
  imports: [CommonModule,],
  declarations: [
    FileInfoComponent
  ],
  exports: [FileInfoComponent]
})
export class FlowDepositUiFileInfoModule {}
