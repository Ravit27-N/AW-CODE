import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonDropdownFacultyComponent } from './button-dropdown-faculty/button-dropdown-faculty.component';
import { FlowTraceabilityFilterComponent } from './flow-traceability-filter/flow-traceability-filter.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { MultiSelectorCheckComponent } from './multi-selector-check/multi-selector-check.component';
import { ButtonDropdownDepositModeComponent } from './button-dropdown-deposit-mode/button-dropdown-deposit-mode.component';
import { ButtonDropdownUsersComponent } from './button-dropdown-users/button-dropdown-users.component';
import { ButtonDropdownStatusComponent } from './button-dropdown-status/button-dropdown-status.component';
import { DateRangeComponent } from './date-range/date-range.component';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { CustomDateRangeHeaderComponent } from './custom-date-range-header/custom-date-range-header.component';

import { ButtonDropdownFillerComponent } from './button-dropdown-filler/button-dropdown-filler.component';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { FlowTraceabilityDataAccessModule } from '@cxm-smartflow/flow-traceability/data-access';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { SharedUiSearchBoxModule } from '@cxm-smartflow/shared/ui/search-box';

const components = [
  ButtonDropdownFacultyComponent,
  FlowTraceabilityFilterComponent,
  MultiSelectorCheckComponent,
  ButtonDropdownDepositModeComponent,
  ButtonDropdownUsersComponent,
  ButtonDropdownStatusComponent,
  DateRangeComponent,
  ButtonDropdownFillerComponent,
  CustomDateRangeHeaderComponent,
];

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    SharedTranslateModule.forRoot(),
    SharedDirectivesTooltipModule,
    SharedCommonTypoModule,
    FlowTraceabilityDataAccessModule,
    SharedUiSearchBoxModule,
  ],
  declarations: [...components],
  exports: [...components],
})
export class FlowTraceabilityUiFlowFilterModule {}
