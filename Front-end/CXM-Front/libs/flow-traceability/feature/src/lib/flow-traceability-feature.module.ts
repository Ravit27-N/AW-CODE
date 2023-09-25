import {
  FlowDocumentShipmentControlService,
  FlowTraceabilityDataAccessModule,
} from '@cxm-smartflow/flow-traceability/data-access';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlowTraceabilityComponent } from './flow-traceability.component';
import { HttpClientModule } from '@angular/common/http';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    FlowTraceabilityDataAccessModule,
    SharedCommonTypoModule,
    HttpClientModule,
    SharedTranslateModule.forRoot(),
    NgDynamicBreadcrumbModule,
    RouterModule.forChild([
      {
        path: '',
        component: FlowTraceabilityComponent,
        children: [
          {
            path: '',
            redirectTo: 'list',
          },
          {
            path: 'list',
            data: {
              breadcrumb: getBreadcrumb().flowTraceability.list,
            },
            loadChildren: () =>
              import(
                '@cxm-smartflow/flow-traceability/ui/flow-traceability-content'
              ).then((m) => m.FlowTraceabilityUiFlowTraceabilityContentModule),
          },
          {
            path: 'document-of-flow-traceability',
            loadChildren: () =>
              import(
                '@cxm-smartflow/flow-traceability/ui/flow-traceability-shipment'
              ).then((m) => m.FlowTraceabilityUiFlowTraceabilityShipmentModule),
          },
          {
            path: 'list-flow-document',
            loadChildren: () =>
              import(
                '@cxm-smartflow/flow-traceability/ui/flow-traceability-shipment'
              ).then((m) => m.FlowTraceabilityUiFlowTraceabilityShipmentModule),
          },
          {
            path: 'flow-detail-deposit',
            data: {
              breadcrumb: getBreadcrumb().flowTraceability
                .flowTraceabilityDepositDetail,
            },
            loadChildren: () =>
              import(
                '@cxm-smartflow/flow-traceability/ui/featured-flow-portal-detail'
              ).then((m) => m.FlowTraceabilityUiFlowHistoryModule),
          },
          {
            path: 'flow-detail-digital',
            data: {
              breadcrumb: getBreadcrumb().flowTraceability.flowDigitalDetail,
            },
            canDeactivate: [FlowDocumentShipmentControlService],
            loadChildren: () =>
              import(
                '@cxm-smartflow/flow-traceability/ui/featured-flow-digital-detail'
              ).then(
                (m) => m.FlowTraceabilityUiFeaturedFlowDigitalDetailModule
              ),
          },
          {
            path: 'flow-document-detail',
            data: {
              breadcrumb: getBreadcrumb().flowTraceability
                .flowDocumentPortalDetail,
            },
            loadChildren: () =>
              import(
                '@cxm-smartflow/flow-traceability/ui/featured-flow-document-detail'
              ).then(
                (m) => m.FlowTraceabilityUiFeaturedFlowDocumentDetailModule
              ),
          },
        ],
      },
    ]),
  ],
  declarations: [FlowTraceabilityComponent],
  exports: [FlowTraceabilityComponent, RouterModule],
})
export class FlowTraceabilityFeatureModule {}
