import { NgModule } from '@angular/core';
import { SharedModule } from '../shared';
import { DashboardRouteModule } from './dashboard-route.module';
import { FeatureDashboardComponent } from './feature-dashboard.component';
import { MatIconModule } from '@angular/material/icon';
import { CalendarRoutingModule } from '../calendar';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { DashboardQuickAccessComponent } from './dashboard-quick-access';
import { DashboardResourceDemandingComponent } from './dashboard-resource-demanding';
import { DashboardTopCandidateComponent } from './dashboard-top-candidate/dashboard-top-candidate.component';
import { CoreModule } from '../core/core.module';
import { MatCardModule } from '@angular/material/card';
import { SlickCarouselModule } from 'ngx-slick-carousel';
import { MatTooltipModule } from '@angular/material/tooltip';
import { DashboardCandidateGraphComponent } from './dashboard-candidate-graph/dashboard-candidate-graph.component';
import {
  DashboardReminderReportComponent,
  ReminderReportCardComponent,
  ReminderReportCardSeeMoreComponent,
  ReminderReportModalComponent,
  ReminderReportService,
} from './dashboard-reminder-report';
import { DashboardInterviewGraphComponent } from './dashboard-interview-graph/dashboard-interview-graph.component';
import { DashboardReminderInterviewComponent } from './dashboard-reminder-interview/dashboard-reminder-interview.component';
import { ChartsModule } from 'ng2-charts';
import {
  ReminderInterviewCardComponent,
  ReminderReviewCardSeeMoreComponent,
} from './dashboard-reminder-interview';

@NgModule({
  imports: [
    SharedModule,
    DashboardRouteModule,
    MatIconModule,
    CalendarRoutingModule,
    CommonModule,
    MatButtonModule,
    MatMenuModule,
    MatSortModule,
    MatTableModule,
    CoreModule,
    MatCardModule,
    SlickCarouselModule,
    MatTooltipModule,
    ChartsModule,
  ],
  declarations: [
    FeatureDashboardComponent,
    DashboardQuickAccessComponent,
    DashboardResourceDemandingComponent,
    DashboardTopCandidateComponent,
    DashboardCandidateGraphComponent,
    DashboardInterviewGraphComponent,
    DashboardReminderReportComponent,
    DashboardReminderInterviewComponent,
    ReminderInterviewCardComponent,
    ReminderReviewCardSeeMoreComponent,
    ReminderReportCardComponent,
    ReminderReportCardSeeMoreComponent,
    ReminderReportModalComponent,
  ],
  providers: [ReminderReportService],
})
export class DemandModule {}
