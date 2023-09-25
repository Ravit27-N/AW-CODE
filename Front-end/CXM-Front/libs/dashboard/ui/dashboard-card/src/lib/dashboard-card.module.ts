import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CampaignReportCardComponent } from './campaign-report-card/campaign-report-card.component';
import { CampaignReportTableCardComponent } from './campaign-report-table-card/campaign-report-table-card.component';
import { CampaignReportProcessingCardComponent } from './campaign-report-processing-card/campaign-report-processing-card.component';
import { CampaignReportEvolutionComponent } from './campaign-report-evolution/campaign-report-evolution.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedUiProgressionCircleModule } from '@cxm-smartflow/shared/ui/progression-circle';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { ChartLegendsComponent } from './campaign-report-evolution/chart-legend.component';
import { DashboardFilterComponent } from './dashboard-filter/dashboard-filter.component';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { GraphChannelEnvoyComponent } from './graph-channel-envoy/graph-channel-envoy.component';
import { GraphDepositModeComponent } from './graph-deposit-mode/graph-deposit-mode.component';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { EmptyPieChartComponent } from './empty-pie-chart/empty-pie-chart.component';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    SharedCommonTypoModule,
    SharedUiProgressionCircleModule,
    SharedTranslateModule.forRoot(),
    NgxChartsModule,
    SharedPipesModule,
    SharedUiSpinnerModule,
  ],
  declarations: [
    CampaignReportCardComponent,
    CampaignReportTableCardComponent,
    CampaignReportProcessingCardComponent,
    CampaignReportEvolutionComponent,
    ChartLegendsComponent,
    DashboardFilterComponent,
    GraphChannelEnvoyComponent,
    GraphDepositModeComponent,
    EmptyPieChartComponent,
  ],
  exports: [
    CampaignReportCardComponent,
    CampaignReportTableCardComponent,
    CampaignReportProcessingCardComponent,
    CampaignReportEvolutionComponent,
    DashboardFilterComponent,
    GraphChannelEnvoyComponent,
    GraphDepositModeComponent,
    EmptyPieChartComponent,
  ],
})
export class DashboarduidashboardCardModule {}
