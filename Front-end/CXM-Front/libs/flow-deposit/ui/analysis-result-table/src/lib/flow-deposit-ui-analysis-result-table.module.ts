import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalysisResultTableComponent } from './analysis-result-table.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedUiPaginatorModule } from '@cxm-smartflow/shared/ui/paginator';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import {FlowDepositUiAddressDestinationPopupModule} from "@cxm-smartflow/flow-deposit/ui/address-destination-popup";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SharedTranslateModule.forRoot(),
    MaterialModule,
    SharedUiPaginatorModule,
    SharedCommonTypoModule,
    FlowDepositUiAddressDestinationPopupModule
  ],
  declarations: [
    AnalysisResultTableComponent
  ],
  exports: [
    AnalysisResultTableComponent
  ]
})
export class FlowDepositUiAnalysisResultTableModule {}
