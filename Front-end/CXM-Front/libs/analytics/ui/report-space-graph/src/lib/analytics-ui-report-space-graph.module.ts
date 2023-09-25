import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GraphVolumeReceivedComponent } from './graph-volume-received/graph-volume-received.component';
import { GraphProductionProgressComponent } from './graph-production-progress/graph-production-progress.component';
import { GlobalProductionDetailsTableComponent } from './global-production-details-table/global-production-details-table.component';
import { DashboarduidashboardCardModule } from '@cxm-smartflow/dashboard/ui/dashboard-card';
import { MatCardModule } from '@angular/material/card';
import { NgxChartsModule, PieChartModule } from '@swimlane/ngx-charts';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { TranslateModule } from '@ngx-translate/core';
import { ChartLegendsComponent } from './graph-volume-received/chart-legend.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { NgxGaugeModule } from 'ngx-gauge';
import { TableProductionDetailComponent } from './table-production-detail/table-production-detail.component';
import {GraphPndMailComponent} from "./graph-pnd-mail/graph-pnd-mail.component";
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { GraphDonutComponent } from './graph-donut/graph-donut.component';
import { NgApexchartsModule } from "ng-apexcharts";

@NgModule({
  imports: [
    CommonModule,
    DashboarduidashboardCardModule,
    MatCardModule,
    PieChartModule,
    SharedCommonTypoModule,
    SharedUiSpinnerModule,
    MaterialModule,
    TranslateModule,
    NgxChartsModule,
    NgxGaugeModule,
    SharedPipesModule,
    NgApexchartsModule,
  ],
  declarations: [
    GraphVolumeReceivedComponent,
    GraphProductionProgressComponent,
    GlobalProductionDetailsTableComponent,
    ChartLegendsComponent,
    TableProductionDetailComponent,
    GraphPndMailComponent,
    GraphDonutComponent,
  ],
  exports: [
    GraphVolumeReceivedComponent,
    GraphProductionProgressComponent,
    GlobalProductionDetailsTableComponent,
    TableProductionDetailComponent,
    GraphPndMailComponent,
    GraphDonutComponent,
  ]
})
export class AnalyticsUiReportSpaceGraphModule {
}
