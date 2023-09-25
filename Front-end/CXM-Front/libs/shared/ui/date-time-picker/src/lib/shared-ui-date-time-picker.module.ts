import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MaterialModule} from "@cxm-smartflow/shared/material";
import { DateTimePickerComponent } from './date-time-picker.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule
  ],
  declarations: [
    DateTimePickerComponent
  ],
  exports: [
    DateTimePickerComponent
  ]
})

export class SharedUiDateTimePickerModule {}
