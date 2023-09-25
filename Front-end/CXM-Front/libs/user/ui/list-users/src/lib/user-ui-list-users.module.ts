import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListUsersComponent } from './list-users.component';
import { RouterModule } from '@angular/router';
import { SharedUiRoundButtonModule } from '@cxm-smartflow/shared/ui/round-button';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { UserUiListUserTableModule } from '@cxm-smartflow/user/ui/list-user-table';
import { UserDataAccessModule } from '@cxm-smartflow/user/data-access';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { SharedUiPaginatorModule } from '@cxm-smartflow/shared/ui/paginator';
import { SharedUiComfirmationMessageModule } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { SharedDirectivesCanVisibilityModule } from '@cxm-smartflow/shared/directives/can-visibility';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';

import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedUiPaginatorModule,
    SharedUiComfirmationMessageModule,
    SharedDirectivesCanVisibilityModule,
    SharedCommonTypoModule,
    SharedTranslateModule.forRoot(),
    SharedUiRoundButtonModule,
    UserUiListUserTableModule,
    UserDataAccessModule,
    RouterModule.forChild([
      {
        path: '',
        component: ListUsersComponent,
        data: {
          breadcrumb: getBreadcrumb().user.list,
        }
      }
    ])
    , SharedPipesModule,
    SharedDataAccessServicesModule,
    NgDynamicBreadcrumbModule,
    MaterialModule
  ],
  declarations: [ListUsersComponent],
  exports: [ListUsersComponent]
})
export class UserUiListUsersModule {
}
