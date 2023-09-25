import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlowHistoryComponent } from './flow-history.component';
import { RouterModule } from '@angular/router';
import { FlowTraceabilityUiFlowTraceabilityPageHeaderModule } from '@cxm-smartflow/flow-traceability/ui/featured-flow-page-header';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { FlowTraceabilityUiFlowEventHistoryModule } from '@cxm-smartflow/flow-traceability/ui/featured-flow-event-history';
import { FlowPortalInfomartionBoardComponent } from './flow-portal-infomartion-board/flow-portal-infomartion-board.component';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild([{ path: '', component: FlowHistoryComponent }]),
    FlowTraceabilityUiFlowTraceabilityPageHeaderModule,
    SharedUiButtonModule,
    FlowTraceabilityUiFlowEventHistoryModule,
    SharedTranslateModule.forRoot(),
  ],
  declarations: [FlowHistoryComponent, FlowPortalInfomartionBoardComponent],
})
export class FlowTraceabilityUiFlowHistoryModule {}
