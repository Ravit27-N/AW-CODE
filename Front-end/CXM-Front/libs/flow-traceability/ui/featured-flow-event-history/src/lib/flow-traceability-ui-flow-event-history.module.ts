import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlowEventHistoryComponent } from './flow-event-history.component';
import { SharedDirectivesInfoTooltipModule } from '@cxm-smartflow/shared/directives/info-tooltip';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedTranslateModule.forRoot(),
    SharedDirectivesInfoTooltipModule,
  ],
  declarations: [FlowEventHistoryComponent],
  exports: [FlowEventHistoryComponent],
})
export class FlowTraceabilityUiFlowEventHistoryModule {}
