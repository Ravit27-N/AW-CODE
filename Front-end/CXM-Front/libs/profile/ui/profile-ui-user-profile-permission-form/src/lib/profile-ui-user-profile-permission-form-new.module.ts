import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { UserProfilePermissionFormComponent } from './user-profile-permission-form/user-profile-permission-form.component';
import { SharedUiFormInputSelectionModule } from '@cxm-smartflow/shared/ui/form-input-selection';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SharedDirectivesTooltipModule,
    MaterialModule,
    SharedUiFormInputSelectionModule,
    SharedTranslateModule.forRoot(),
  ],
  declarations: [UserProfilePermissionFormComponent],
  exports: [UserProfilePermissionFormComponent],
})
export class ProfileUiUserProfilePermissionFormNewModule {}
