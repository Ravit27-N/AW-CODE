import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserComponent } from './user.component';
import { RouterModule } from '@angular/router';
import { UserDataAccessModule } from '@cxm-smartflow/user/data-access';
import { ProfileUiProfileNavigatorModule } from '@cxm-smartflow/profile/ui/profile-navigator';
import { SharedDirectivesCanVisibilityModule } from '@cxm-smartflow/shared/directives/can-visibility';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    ProfileUiProfileNavigatorModule,
    UserDataAccessModule,
    SharedDirectivesCanVisibilityModule,
    SharedTranslateModule.forRoot(),
    NgDynamicBreadcrumbModule,
    RouterModule.forChild([
      {
        path: '',
        component: UserComponent,
        children: [
          {
            path: 'users',
            redirectTo: 'list-users',
          },
          {
            path: 'list-user',
            data: {
              breadcrumb: getBreadcrumb().user.list,
            },
            loadChildren: () =>
              import('@cxm-smartflow/user/ui/list-users').then(
                (m) => m.UserUiListUsersModule
              ),
          },
          {
            path: 'create-user',
            data: {
              breadcrumb: getBreadcrumb().user.create,
            },
            loadChildren: () =>
              import('@cxm-smartflow/user/ui/featured-create-user').then(
                (m) => m.UserUiCreateUserModule
              ),
          },
          {
            path: 'update-user',
            data: {
              breadcrumb: getBreadcrumb().user.edit,
            },
            loadChildren: () =>
              import('@cxm-smartflow/user/ui/featured-update-user').then(
                (m) => m.UserUiUpdateUserModule
              ),
          },
          {
            path: `update-batch-user`,
            data: {
              breadcrumb: getBreadcrumb().user.editBatch,
            },
            loadChildren: () =>
              import('@cxm-smartflow/user/ui/featured-update-user').then(
                (m) => m.UserUiUpdateUserModule
              ),
          },
        ],
      },
    ]),
  ],
  declarations: [UserComponent],
})
export class UserFeatureModule {}
