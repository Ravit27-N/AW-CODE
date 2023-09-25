import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UpdateProfileFeatureComponent } from './update-profile-feature.component';
import { ProfileUiUserProfilePermissionFormNewModule } from '@cxm-smartflow/profile/ui/profile-ui-user-profile-permission-form';
import { RouterModule } from '@angular/router';
import { ProfileUiProfileHeaderModule } from '@cxm-smartflow/profile/ui/profile-header';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { LockableFormGuard } from '@cxm-smartflow/profile/data-access';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    ProfileUiUserProfilePermissionFormNewModule,
    ProfileUiProfileHeaderModule,
    SharedUiButtonModule,
    SharedTranslateModule.forRoot(),
    RouterModule.forChild([
      {
        path: ':id',
        component: UpdateProfileFeatureComponent,
        canDeactivate: [LockableFormGuard],
      },
    ]),
  ],
  declarations: [UpdateProfileFeatureComponent],
  exports: [UpdateProfileFeatureComponent],
  providers: [LockableFormGuard],
})
export class ProfileUiUpdateProfileFeatureModule {}
