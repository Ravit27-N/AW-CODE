import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IdentificationTableComponent } from './identification-table.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { HttpClientModule } from '@angular/common/http';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    HttpClientModule,
    SharedTranslateModule.forRoot()
  ],
  declarations: [
    IdentificationTableComponent
  ],
  exports: [
    IdentificationTableComponent
  ]
})
export class FlowDepositUiIdentificationTableModule {}

