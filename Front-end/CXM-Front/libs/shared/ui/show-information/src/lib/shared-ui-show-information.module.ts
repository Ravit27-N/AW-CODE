import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ShowInformationComponent } from './show-information/show-information.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
  ],
  declarations: [
    ShowInformationComponent
  ],
  exports: [ShowInformationComponent]
})
export class SharedUiShowInformationModule {}
