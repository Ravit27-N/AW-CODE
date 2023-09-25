import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { DropdownFilterChannelComponent } from './dropdown-filter-channel/dropdown-filter-channel.component';
import { TranslateModule } from '@ngx-translate/core';
import { MultiCheckComponent } from './multi-check/multi-check.component';
import { DropdownFilterUserComponent } from './dropdown-filter-user/dropdown-filter-user.component';
import { DropdownFilterDepositModeComponent } from './dropdown-filter-deposit-mode/dropdown-filter-deposit-mode.component';

@NgModule({
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MaterialModule, TranslateModule],
  declarations: [DropdownFilterChannelComponent, MultiCheckComponent, DropdownFilterUserComponent, DropdownFilterDepositModeComponent],
  exports: [DropdownFilterChannelComponent, MultiCheckComponent, DropdownFilterUserComponent, DropdownFilterDepositModeComponent]
})
export class SharedUiDropdownFilterCriteriaModule {
}
