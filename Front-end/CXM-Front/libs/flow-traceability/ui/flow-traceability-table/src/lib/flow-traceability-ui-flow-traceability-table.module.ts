import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { SharedComfirmDialogModule } from '@cxm-smartflow/shared/comfirm-dialog';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedUiComfirmationMessageModule } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { SharedUiPaginatorModule } from '@cxm-smartflow/shared/ui/paginator';
import { SharedUiSearchModule } from '@cxm-smartflow/shared/ui/search';
import { FlowTraceabilityTableComponent } from './flow-traceability-table.component';
import { SharedDirectivesCanVisibilityModule } from '@cxm-smartflow/shared/directives/can-visibility';
import { SharedDirectivesCanModificationModule } from '@cxm-smartflow/shared/directives/can-modification';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { FlowTraceabilityUiFlowFilterModule } from '@cxm-smartflow/flow-traceability/ui/flow-filter';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SharedTranslateModule.forRoot(),
    MaterialModule,
    SharedUiPaginatorModule,
    SharedComfirmDialogModule,
    SharedUiComfirmationMessageModule,
    SharedDataAccessServicesModule,
    SharedUiSearchModule,
    SharedDirectivesCanVisibilityModule,
    SharedDirectivesCanModificationModule,
    SharedUiSpinnerModule,
    FlowTraceabilityUiFlowFilterModule,
    SharedCommonTypoModule
  ],
  declarations: [
    FlowTraceabilityTableComponent
  ],
  exports: [
    FlowTraceabilityTableComponent
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'en-GB' }
  ]
})
export class FlowTraceabilityUiFlowTraceabilityTableModule {}
