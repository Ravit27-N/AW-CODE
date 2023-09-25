import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalyticsFeatureComponent } from './analytics-feature.component';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { RouterModule } from '@angular/router';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { AnalyticsDataAccessModule } from '@cxm-smartflow/analytics/data-access';
import { CanDeactivateGuard } from './analytics-feature-can-deactivate';
import {SharedCommonTypoModule} from "@cxm-smartflow/shared/common-typo";
import {SharedUiButtonModule} from "@cxm-smartflow/shared/ui/button";

@NgModule({
  imports: [
    CommonModule,
    NgDynamicBreadcrumbModule,
    AnalyticsDataAccessModule,
    SharedTranslateModule.forRoot(),
    RouterModule.forChild([
      {
        path: '',
        component: AnalyticsFeatureComponent,
        canDeactivate: [CanDeactivateGuard],
        children: [
          {
            path: '',
            redirectTo: 'report-space',
          },
          {
            path: 'report-space',
            data: {
              breadcrumb: getBreadcrumb().analytics.list,
            },
            loadChildren: () => import('@cxm-smartflow/analytics/ui/feature-reporting-space').then((m) => m.AnalyticsUiFeatureReportingSpaceModule),
          },
          {
            path: 'report-postal',
            data: {
              breadcrumb: getBreadcrumb().analytics.list,
            },
            loadChildren: () => import('@cxm-smartflow/analytics/ui/feature-reporting-postal').then((m) => m.AnalyticsUiFeatureReportingPostalModule),
          },
          {
            path: 'report-email',
            data: {
              breadcrumb: getBreadcrumb().analytics.list,
            },
            loadChildren: () => import('@cxm-smartflow/analytics/ui/feature-reporting-email').then((m) => m.AnalyticsUiFeatureReportingEmailModule),
          },
          {
            path: 'report-sms',
            data: {
              breadcrumb: getBreadcrumb().analytics.list,
            },
            loadChildren: () => import('@cxm-smartflow/analytics/ui/feature-reporting-sms').then((m) => m.AnalyticsUiFeatureReportingSmsModule),
          },
        ],
      },
    ]),
    SharedCommonTypoModule,
    SharedUiButtonModule,
  ],
  declarations: [AnalyticsFeatureComponent],
  providers: [CanDeactivateGuard]
})
export class AnalyticsFeatureModule {}
