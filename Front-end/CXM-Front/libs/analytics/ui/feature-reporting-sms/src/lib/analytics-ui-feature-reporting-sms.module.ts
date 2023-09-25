import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FeatureReportingSmsComponent} from './feature-reporting-sms.component';
import {AnalyticsUiReportSpaceFilteringModule} from "@cxm-smartflow/analytics/ui/report-space-filtering";
import {AnalyticsUiReportSpaceGraphModule} from "@cxm-smartflow/analytics/ui/report-space-graph";
import {AnalyticsUiReportSpaceNavigationModule} from "@cxm-smartflow/analytics/ui/report-space-navigation";
import {NgDynamicBreadcrumbModule} from "ng-dynamic-breadcrumb";
import {SharedCommonTypoModule} from "@cxm-smartflow/shared/common-typo";
import {SharedUiButtonModule} from "@cxm-smartflow/shared/ui/button";
import {TranslateModule} from "@ngx-translate/core";
import {RouterModule} from "@angular/router";

@NgModule({
  imports: [CommonModule,
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
        component: FeatureReportingSmsComponent,
      },
    ]),],
  declarations: [
    FeatureReportingSmsComponent
  ],
})
export class AnalyticsUiFeatureReportingSmsModule {}
