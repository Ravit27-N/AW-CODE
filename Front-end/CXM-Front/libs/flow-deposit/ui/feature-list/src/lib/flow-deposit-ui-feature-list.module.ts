import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostalComponent } from './postal/postal.component';
import { DigitalComponent } from './digital/digital.component';
import { RouterModule, Routes } from '@angular/router';
import { FlowDepositUiHeaderModule } from '@cxm-smartflow/flow-deposit/ui/header';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FlowDepositDataAccessModule } from '@cxm-smartflow/flow-deposit/data-access';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FlowDepositFilterComponent } from './flow-deposit-filter/flow-deposit-filter.component';
import { SharedUiPaginatorModule } from '@cxm-smartflow/shared/ui/paginator';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedUiDropdownFilterCriteriaModule } from '@cxm-smartflow/shared/ui/dropdown-filter-criterial';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { SharedUiSearchBoxModule } from '@cxm-smartflow/shared/ui/search-box';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'postal',
  },
  {
    path: 'postal',
    data: {
      breadcrumb: getBreadcrumb().deposit.listPortal,
    },
    component: PostalComponent,
  },
  {
    path: 'digital',
    component: DigitalComponent,
  },
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FlowDepositUiHeaderModule,
    SharedCommonTypoModule,
    NgDynamicBreadcrumbModule,
    MaterialModule,
    FlowDepositDataAccessModule,
    SharedTranslateModule.forRoot(),
    ReactiveFormsModule,
    FormsModule,
    SharedUiPaginatorModule,
    SharedDirectivesTooltipModule,
    SharedUiDropdownFilterCriteriaModule,
    SharedUiSearchBoxModule,
  ],
  declarations: [PostalComponent, DigitalComponent, FlowDepositFilterComponent],
})
export class FlowDepositUiFeatureListModule {}
