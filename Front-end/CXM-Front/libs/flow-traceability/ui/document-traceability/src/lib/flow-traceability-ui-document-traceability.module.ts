import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentTraceabilityComponent } from './document-traceability.component';
import { FlowTraceabilityUiDocumentTraceabilityTableModule } from '@cxm-smartflow/flow-traceability/ui/document-traceability-table';
import { HttpClientModule } from '@angular/common/http';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { FlowTraceabilityUiFlowFilterModule } from '@cxm-smartflow/flow-traceability/ui/flow-filter';
import { FlowTraceabilityUiFlowTraceabilityPageHeaderModule } from '@cxm-smartflow/flow-traceability/ui/featured-flow-page-header';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { FlowTraceabilityDataAccessModule } from '@cxm-smartflow/flow-traceability/data-access';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    FlowTraceabilityUiDocumentTraceabilityTableModule,
    SharedCommonTypoModule,
    FlowTraceabilityUiFlowFilterModule,
    SharedUiButtonModule,
    FlowTraceabilityUiFlowTraceabilityPageHeaderModule,
    HttpClientModule,
    FlowTraceabilityDataAccessModule,
    SharedTranslateModule.forRoot(),
  ],
  declarations: [DocumentTraceabilityComponent],
  exports: [DocumentTraceabilityComponent],
})
export class FlowTraceabilityUiDocumentTraceabilityModule {}
