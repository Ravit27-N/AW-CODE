import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CircleProgressionComponent } from './circle-progression.component';
import { CircleProgressionControlComponent } from './circle-progression-control/circle-progression-control.component';

@NgModule({
  imports: [CommonModule],
  declarations: [CircleProgressionComponent, CircleProgressionControlComponent],
  exports: [CircleProgressionComponent, CircleProgressionControlComponent],
})
export class SharedUiCircleProgressionModule {}
