import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentTraceabilityTableComponent } from './document-traceability-table.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedUiPaginatorModule } from '@cxm-smartflow/shared/ui/paginator';
import {
  CXMDateFormatPipe,
  SharedPipesModule,
} from '@cxm-smartflow/shared/pipes';
import { HttpClientModule } from '@angular/common/http';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { SharedDirectivesCanVisibilityModule } from '@cxm-smartflow/shared/directives/can-visibility';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedTranslateModule.forRoot(),
    HttpClientModule,
    MaterialModule,
    SharedUiPaginatorModule,
    SharedPipesModule,
    SharedDataAccessServicesModule,
    SharedDirectivesCanVisibilityModule,
    SharedUiSpinnerModule,
    SharedCommonTypoModule,
  ],
  declarations: [DocumentTraceabilityTableComponent],
  exports: [DocumentTraceabilityTableComponent],
  providers: [CXMDateFormatPipe],
})
export class FlowTraceabilityUiDocumentTraceabilityTableModule {}
