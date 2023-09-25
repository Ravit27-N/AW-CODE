import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListEnvelopeReferencesComponent } from './list-envelope-references.component';
 import { RouterModule } from '@angular/router';
import { SharedUiRoundButtonModule } from '@cxm-smartflow/shared/ui/round-button';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { SharedUiPaginatorModule } from '@cxm-smartflow/shared/ui/paginator';
import { SharedUiComfirmationMessageModule } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { SharedDirectivesCanVisibilityModule } from '@cxm-smartflow/shared/directives/can-visibility';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import {EnvelopeReferenceDataAccessModule} from "@cxm-smartflow/envelope-reference/data-access";
import {ListEnvelopeReferenceTableModule} from "../../../list-envelope-reference-table/src";

@NgModule({
  imports: [
    CommonModule,
    SharedUiPaginatorModule,
    SharedUiComfirmationMessageModule,
    SharedDirectivesCanVisibilityModule,
    SharedCommonTypoModule,
    SharedTranslateModule.forRoot(),
    SharedUiRoundButtonModule,
    EnvelopeReferenceDataAccessModule,
    RouterModule.forChild([
      {
        path: '',
        component: ListEnvelopeReferencesComponent,
        data: {
          breadcrumb: getBreadcrumb().setting.list,
        }
      }
    ])
    , SharedPipesModule,
    SharedDataAccessServicesModule,
    NgDynamicBreadcrumbModule,
    MaterialModule,
    ListEnvelopeReferenceTableModule
  ],
  declarations: [ListEnvelopeReferencesComponent],
  exports: [ListEnvelopeReferencesComponent]
})
export class FeaturedListEnvelopeReferencesModule {
}
