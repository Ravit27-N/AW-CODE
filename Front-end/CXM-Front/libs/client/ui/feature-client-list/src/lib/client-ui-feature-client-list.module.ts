import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { ClientListPageComponent } from './client-list-page/client-list-page.component';
import { ClientListComponent } from './client-list/client-list.component';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

const routes: Routes = [
  {
    path: '',
    component: ClientListPageComponent,
    data: {
      breadcrumb: getBreadcrumb().client.list
    }
  }
]


@NgModule({
  imports: [
    CommonModule, RouterModule.forChild(routes),
    SharedCommonTypoModule,
    SharedTranslateModule.forRoot(),
    NgDynamicBreadcrumbModule,
    MaterialModule,
    SharedPipesModule
  ],
  declarations: [
    ClientListPageComponent,
    ClientListComponent
  ],
})
export class ClientUiFeatureClientListModule {}
