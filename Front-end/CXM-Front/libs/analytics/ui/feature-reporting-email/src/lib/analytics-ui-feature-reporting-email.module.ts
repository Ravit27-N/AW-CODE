import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureReportingEmailComponent } from './feature-reporting-email.component';
import { AnalyticsUiReportSpaceFilteringModule } from '@cxm-smartflow/analytics/ui/report-space-filtering';
import { AnalyticsUiReportSpaceGraphModule } from '@cxm-smartflow/analytics/ui/report-space-graph';
import { AnalyticsUiReportSpaceNavigationModule } from '@cxm-smartflow/analytics/ui/report-space-navigation';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { TranslateModule } from '@ngx-translate/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    CommonModule,
    AnalyticsUiReportSpaceFilteringModule,
    AnalyticsUiReportSpaceGraphModule,
    AnalyticsUiReportSpaceNavigationModule,
    NgDynamicBreadcrumbModule,
    SharedCommonTypoModule,
    SharedUiButtonModule,
    TranslateModule,
    RouterModule.forChild([
      {
        path: '',
        component: FeatureReportingEmailComponent,
      },
    ]),
  ],
  declarations: [FeatureReportingEmailComponent],
})
export class AnalyticsUiFeatureReportingEmailModule {}
