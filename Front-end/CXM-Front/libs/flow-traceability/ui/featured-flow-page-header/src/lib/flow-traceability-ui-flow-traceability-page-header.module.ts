import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlowTraceabilityPageHeaderComponent } from './flow-traceability-page-header.component';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedTranslateModule.forRoot(),
    SharedCommonTypoModule,
    NgDynamicBreadcrumbModule,
    SharedUiButtonModule,
  ],
  declarations: [FlowTraceabilityPageHeaderComponent],
  exports: [FlowTraceabilityPageHeaderComponent],
})
export class FlowTraceabilityUiFlowTraceabilityPageHeaderModule {}
