import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentOfFlowTraceabilityComponent } from './document-of-flow-traceability.component';
import { RouterModule } from '@angular/router';
import { FlowTraceabilityUiFlowTraceabilityTableModule } from '@cxm-smartflow/flow-traceability/ui/flow-traceability-table';
import { FlowTraceabilityUiDocumentTraceabilityModule } from '@cxm-smartflow/flow-traceability/ui/document-traceability';
import { FlowTraceabilityUiFlowTraceabilityPageHeaderModule } from '@cxm-smartflow/flow-traceability/ui/featured-flow-page-header';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { FlowDocumentShipmentControlService } from '@cxm-smartflow/flow-traceability/data-access';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { SharedCommonTypoModule } from "../../../../../shared/common-typo/src/lib/shared-common-typo.module";
import { SharedUiButtonModule } from "../../../../../shared/ui/button/src/lib/shared-ui-button.module";

@NgModule({
    declarations: [DocumentOfFlowTraceabilityComponent],
    imports: [
        CommonModule,
        SharedTranslateModule.forRoot(),
        FlowTraceabilityUiFlowTraceabilityTableModule,
        FlowTraceabilityUiDocumentTraceabilityModule,
        FlowTraceabilityUiFlowTraceabilityPageHeaderModule,
        CommonModule,
        RouterModule.forChild([
            {
                path: '',
                data: {
                    breadcrumb: getBreadcrumb().flowTraceability.flowTracking,
                },
                component: DocumentOfFlowTraceabilityComponent,
            },
            {
                path: 'view-shipment',
                data: {
                    breadcrumb: getBreadcrumb().flowTraceability.flowTracking,
                },
                canDeactivate: [FlowDocumentShipmentControlService],
                component: DocumentOfFlowTraceabilityComponent,
            },
        ]),
        SharedCommonTypoModule,
        SharedUiButtonModule
    ]
})
export class FlowTraceabilityUiFlowTraceabilityShipmentModule {}
