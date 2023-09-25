import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StepperComponent } from './stepper.component';
import { MatStepperModule } from '@angular/material/stepper';
import { TranslateModule } from '@ngx-translate/core';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  imports: [CommonModule, MatStepperModule, TranslateModule, MatIconModule],
  declarations: [
    StepperComponent
  ],
  exports: [
    StepperComponent
  ]
})
export class SharedUiStepperModule {}
