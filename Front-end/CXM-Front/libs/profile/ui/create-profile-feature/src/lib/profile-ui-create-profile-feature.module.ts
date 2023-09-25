import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CreateProfileFeatureComponent } from './create-profile-feature.component';
import { RouterModule } from '@angular/router';
import { ProfileUiUserProfilePermissionFormNewModule } from '@cxm-smartflow/profile/ui/profile-ui-user-profile-permission-form';
import { ProfileUiProfileHeaderModule } from '@cxm-smartflow/profile/ui/profile-header';
import { LockableFormGuard } from '@cxm-smartflow/profile/data-access';

@NgModule({
  imports: [
    CommonModule,
    ProfileUiUserProfilePermissionFormNewModule,
    ProfileUiProfileHeaderModule,
    RouterModule.forChild([
      {
        path: '',
        component: CreateProfileFeatureComponent,
        canDeactivate: [LockableFormGuard],
      },
    ]),
  ],
  declarations: [CreateProfileFeatureComponent],
  exports: [CreateProfileFeatureComponent],
  providers: [LockableFormGuard],
})
export class ProfileUiCreateProfileFeatureModule {}
