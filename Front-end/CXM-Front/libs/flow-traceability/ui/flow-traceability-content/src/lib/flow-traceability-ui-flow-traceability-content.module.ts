import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlowTraceabilityContentComponent } from './flow-traceability-content.component';
import { FlowTraceabilityUiFlowTraceabilityTableModule } from '@cxm-smartflow/flow-traceability/ui/flow-traceability-table';
import { FlowTraceabilityUiDocumentTraceabilityModule } from '@cxm-smartflow/flow-traceability/ui/document-traceability';
import { FlowTraceabilityUiFlowTraceabilityPageHeaderModule } from '@cxm-smartflow/flow-traceability/ui/featured-flow-page-header';
import { RouterModule } from '@angular/router';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedTranslateModule.forRoot(),
    FlowTraceabilityUiFlowTraceabilityTableModule,
    FlowTraceabilityUiDocumentTraceabilityModule,
    FlowTraceabilityUiFlowTraceabilityPageHeaderModule,
    RouterModule.forChild([
      {
        path: '',
        component: FlowTraceabilityContentComponent,
      },
    ]),
  ],
  declarations: [FlowTraceabilityContentComponent],
  exports: [FlowTraceabilityContentComponent],
})
export class FlowTraceabilityUiFlowTraceabilityContentModule {}
