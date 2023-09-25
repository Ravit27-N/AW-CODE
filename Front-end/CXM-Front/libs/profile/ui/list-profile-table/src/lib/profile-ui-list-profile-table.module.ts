import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListProfileTableComponent } from './list-profile-table.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedUiPaginatorModule } from '@cxm-smartflow/shared/ui/paginator';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedUiComfirmationMessageModule } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    SharedTranslateModule.forRoot(),
    SharedDirectivesTooltipModule,
    SharedUiPaginatorModule,
    SharedUiComfirmationMessageModule,
    SharedCommonTypoModule,
  ],
  declarations: [
    ListProfileTableComponent
  ],
  exports: [
    ListProfileTableComponent
  ]
})
export class ProfileUiListProfileTableModule {}
