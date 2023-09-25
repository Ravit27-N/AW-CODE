import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListEnvelopeReferenceTableComponent } from './list-envelope-reference-table.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { UserDataAccessModule } from '@cxm-smartflow/user/data-access';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedUiPaginatorModule } from '@cxm-smartflow/shared/ui/paginator';
import { SharedUiComfirmationMessageModule } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { SharedUiSearchBoxModule } from '@cxm-smartflow/shared/ui/search-box';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    SharedTranslateModule.forRoot(),
    UserDataAccessModule,
    SharedDirectivesTooltipModule,
    SharedUiPaginatorModule,
    SharedUiComfirmationMessageModule,
    SharedCommonTypoModule,
    SharedUiSearchBoxModule,
  ],
  declarations: [
    ListEnvelopeReferenceTableComponent,
  ],
  exports: [ListEnvelopeReferenceTableComponent],
})
export class ListEnvelopeReferenceTableModule {}
