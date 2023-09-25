import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { EspaceValidationListPageComponent } from './espace-validation-list-page/espace-validation-list-page.component';
import { EspaceValidationConsultPageComponent } from './espace-validation-consult-page/espace-validation-consult-page.component';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { EspaceEnvoyListComponent } from './espace-envoy-list';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedUiComfirmationMessageModule } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { ApprovalDataAccessModule } from '@cxm-smartflow/approval/data-access';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { SharedCommonCriteriaModule } from '@cxm-smartflow/shared/common-criteria';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { SharedUiSearchBoxModule } from '@cxm-smartflow/shared/ui/search-box';

const routes: Routes = [
  {
    path: '',
    component: EspaceValidationListPageComponent,
    data: {
      breadcrumb: getBreadcrumb().espace.list,
    },
  },
  {
    path: 'consult/:id/:name',
    component: EspaceValidationConsultPageComponent,
    data: {
      breadcrumb: getBreadcrumb().espace.consult,
    },
  },
];


@NgModule({
  imports: [
    CommonModule,
    SharedCommonTypoModule,
    RouterModule.forChild(routes),
    SharedTranslateModule.forRoot(),
    NgDynamicBreadcrumbModule,
    ReactiveFormsModule,
    MaterialModule,
    FormsModule,
    SharedUiComfirmationMessageModule,
    ApprovalDataAccessModule,
    SharedDirectivesTooltipModule,
    SharedPipesModule,
    SharedUiButtonModule,
    SharedCommonCriteriaModule,
    SharedUiSearchBoxModule,
  ],
  declarations: [
    EspaceValidationListPageComponent,
    EspaceValidationConsultPageComponent,
    EspaceEnvoyListComponent,
  ],
})
export class ApprovalUiFeatureEspaceValidationListModule {}
