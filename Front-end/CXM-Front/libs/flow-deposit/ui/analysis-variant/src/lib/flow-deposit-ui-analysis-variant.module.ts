import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalysisVariantComponent } from './analysis-variant.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SharedTranslateModule.forRoot(),
    MaterialModule
  ],
  declarations: [
    AnalysisVariantComponent
  ],
  exports: [
    AnalysisVariantComponent
  ]
})
export class FlowDepositUiAnalysisVariantModule {}
