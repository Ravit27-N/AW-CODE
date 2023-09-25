import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChangePasswordFormComponent } from './change-password-form/change-password-form.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatPasswordStrengthModule } from '@angular-material-extensions/password-strength';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    SharedTranslateModule.forRoot(),
    SharedDirectivesTooltipModule,
    MatSlideToggleModule,
    MatPasswordStrengthModule
  ],
  declarations: [
    ChangePasswordFormComponent
  ],
  exports: [
    ChangePasswordFormComponent
  ]
})
export class AuthUiFeatureChangePasswordModule {
}
