import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureReportingPostalComponent } from './feature-reporting-postal.component';
import { RouterModule } from '@angular/router';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { TranslateModule } from '@ngx-translate/core';
import { AnalyticsUiReportSpaceNavigationModule } from '@cxm-smartflow/analytics/ui/report-space-navigation';
import { AnalyticsUiReportSpaceFilteringModule } from '@cxm-smartflow/analytics/ui/report-space-filtering';
import { AnalyticsUiReportSpaceGraphModule } from '@cxm-smartflow/analytics/ui/report-space-graph';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild([
      {
        path: '',
        component: FeatureReportingPostalComponent,
      },
    ]),
    NgDynamicBreadcrumbModule,
    SharedCommonTypoModule,
    TranslateModule,
    AnalyticsUiReportSpaceNavigationModule,
    AnalyticsUiReportSpaceFilteringModule,
    AnalyticsUiReportSpaceGraphModule,
    SharedUiButtonModule,
  ],
  declarations: [FeatureReportingPostalComponent],
})
export class AnalyticsUiFeatureReportingPostalModule {}
