import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportSpaceNavigationComponent } from './report-space-navigation.component';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  imports: [CommonModule, TranslateModule],
  declarations: [ReportSpaceNavigationComponent],
  exports: [ReportSpaceNavigationComponent],
})
export class AnalyticsUiReportSpaceNavigationModule {}
