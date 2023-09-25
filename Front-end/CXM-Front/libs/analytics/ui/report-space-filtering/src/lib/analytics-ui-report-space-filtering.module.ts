import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportSpaceFilteringComponent } from './report-space-filtering.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { TranslateModule } from '@ngx-translate/core';
import { FilterChannelCategoryComponent } from './filter-channel-category/filter-channel-category.component';
import { FilterCalendarComponent } from './filter-calendar/filter-calendar.component';
import { FilterFillersComponent } from './filter-fillers/filter-fillers.component';
import { MatDividerModule } from '@angular/material/divider';
import { FilterCalendarLevelPickerComponent } from './filter-calendar-level-picker/filter-calendar-level-picker.component';
import { CalendarContentComponent } from './filter-calendar-level-picker/calendar-content.component';
import { CalendarPickerComponent } from './filter-calendar-level-picker/calendar-picker.component';
import { FilterCategoryComponent } from './filter-category/filter-category.component';
import { FilterFillerGroupComponent } from './filter-filler-group/filter-filler-group.component';
import { MatRippleModule } from '@angular/material/core';
import { MatRadioModule } from '@angular/material/radio';
import { FilterFillerRadioComponent } from './filter-filler-radio/filter-filler-radio.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    MatDatepickerModule,
    MatIconModule,
    MatMenuModule,
    ReactiveFormsModule,
    SharedCommonTypoModule,
    TranslateModule,
    MatDividerModule,
    MatRippleModule,
    MatRadioModule,
  ],
  declarations: [
    ReportSpaceFilteringComponent,
    FilterChannelCategoryComponent,
    FilterCalendarComponent,
    FilterFillersComponent,
    FilterCalendarLevelPickerComponent,
    CalendarContentComponent,
    CalendarPickerComponent,
    FilterCategoryComponent,
    FilterFillerGroupComponent,
    FilterFillerRadioComponent,
  ],
  exports: [ReportSpaceFilteringComponent],
})
export class AnalyticsUiReportSpaceFilteringModule {}
