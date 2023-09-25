import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProgressionLineComponent } from './progression-line.component';

@NgModule({
  imports: [CommonModule],
  declarations: [
    ProgressionLineComponent
  ],
  exports: [ProgressionLineComponent]
})
export class SharedUiProgressionLineModule {}
