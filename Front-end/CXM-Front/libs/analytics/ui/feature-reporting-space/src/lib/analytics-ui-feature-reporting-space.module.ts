import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureReportingSpaceComponent } from './feature-reporting-space.component';
import { RouterModule } from '@angular/router';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { TranslateModule } from '@ngx-translate/core';
import { MatDividerModule } from '@angular/material/divider';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { AnalyticsUiReportSpaceNavigationModule } from '@cxm-smartflow/analytics/ui/report-space-navigation';
import { SharedUiProgressionCircleModule } from '@cxm-smartflow/shared/ui/progression-circle';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AnalyticsUiReportSpaceFilteringModule } from '@cxm-smartflow/analytics/ui/report-space-filtering';
import { AnalyticsDataAccessModule } from '@cxm-smartflow/analytics/data-access';
import { AnalyticsUiReportSpaceGraphModule } from '@cxm-smartflow/analytics/ui/report-space-graph';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild([
      {
        path: '',
        component: FeatureReportingSpaceComponent,
      },
    ]),
    NgDynamicBreadcrumbModule,
    SharedCommonTypoModule,
    MatDividerModule,
    TranslateModule,
    SharedUiButtonModule,
    AnalyticsUiReportSpaceNavigationModule,
    SharedUiProgressionCircleModule,
    MatDatepickerModule,
    MatIconModule,
    MatMenuModule,
    FormsModule,
    SharedPipesModule,
    ReactiveFormsModule,
    MatTooltipModule,
    AnalyticsUiReportSpaceFilteringModule,
    AnalyticsDataAccessModule,
    AnalyticsUiReportSpaceGraphModule,
  ],
  declarations: [
    FeatureReportingSpaceComponent,
  ],
})
export class AnalyticsUiFeatureReportingSpaceModule {}
