import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListUserTableComponent } from './list-user-table.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { UserDataAccessModule } from '@cxm-smartflow/user/data-access';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedUiPaginatorModule } from '@cxm-smartflow/shared/ui/paginator';
import { SharedUiComfirmationMessageModule } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { ListUserProfileFilterComponent } from './list-user-profile-filter/list-user-profile-filter.component';
import { ListAdminUserFilterComponent } from './list-admin-user-filter/list-admin-user-filter.component';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { ListUserClientFilterComponent } from './list-user-client-filter/list-user-client-filter.component';
import { ListUserDivisionFilterComponent } from './list-user-division-filter/list-user-division-filter.component';
import { ListUserServiceFilterComponent } from './list-user-service-filter/list-user-service-filter.component';
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
    ListUserTableComponent,
    ListUserProfileFilterComponent,
    ListAdminUserFilterComponent,
    ListUserClientFilterComponent,
    ListUserDivisionFilterComponent,
    ListUserServiceFilterComponent,
  ],
  exports: [ListUserTableComponent],
})
export class UserUiListUserTableModule {}
