import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileComponent } from './profile.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { ProfileDataAccessModule } from '@cxm-smartflow/profile/data-access';
import { ProfileUiProfileNavigatorModule } from '@cxm-smartflow/profile/ui/profile-navigator';
import { SharedUiRoundButtonModule } from '@cxm-smartflow/shared/ui/round-button';
import { UserDataAccessModule } from '@cxm-smartflow/user/data-access';
import { SharedDirectivesCanVisibilityModule } from '@cxm-smartflow/shared/directives/can-visibility';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    ProfileDataAccessModule,
    UserDataAccessModule,
    SharedUiRoundButtonModule,
    ProfileUiProfileNavigatorModule,
    SharedDirectivesCanVisibilityModule,
    NgDynamicBreadcrumbModule,
    SharedTranslateModule.forRoot(),
    RouterModule.forChild([
      {
        path: '',
        component: ProfileComponent,
        children: [
          {
            path: '',
            redirectTo: 'list-profiles'
          },
          {
            path: 'list-profiles',
            data: {
              breadcrumb: getBreadcrumb().profile.list
            },
            loadChildren: () => import('@cxm-smartflow/profile/ui/list-profiles').then(
              (m) => m.ProfileUiListProfilesModule
            )
          },
          {
            path: 'create-profile',
            data: {
              breadcrumb: getBreadcrumb().profile.create
            },
            loadChildren: () => import('@cxm-smartflow/profile/ui/create-profile-feature').then(m => m.ProfileUiCreateProfileFeatureModule)
          },
          {
            path: 'update-profile',
            data: {
              breadcrumb: getBreadcrumb().profile.edit
            },
            loadChildren: () => import('@cxm-smartflow/profile/ui/update-profile-feature').then(m => m.ProfileUiUpdateProfileFeatureModule)
          }
        ]
      }
    ])
  ],
  declarations: [
    ProfileComponent
  ]
})
export class ProfileFeatureModule {
}
