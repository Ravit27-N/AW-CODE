import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StepComponent } from './step.component';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  imports: [CommonModule, TranslateModule],
  declarations: [
    StepComponent
  ],
  exports: [StepComponent]
})
export class SharedUiStepModule {}
