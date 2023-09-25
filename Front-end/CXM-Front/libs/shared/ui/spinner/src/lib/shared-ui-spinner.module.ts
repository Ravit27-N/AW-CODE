import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SpinnerComponent } from './spinner/spinner.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule
  ],
  declarations: [
    SpinnerComponent
  ],
  exports: [SpinnerComponent]
})
export class SharedUiSpinnerModule {}
