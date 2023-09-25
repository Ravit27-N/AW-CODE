import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Route, RouterModule } from '@angular/router';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { SharedUiInputModule } from '@cxm-smartflow/shared/ui/input';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { AuthDataAccessModule } from '@cxm-smartflow/auth/data-access';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedUiLayoutModule } from '@cxm-smartflow/shared/ui/layout';
import { MatPasswordStrengthModule } from '@angular-material-extensions/password-strength';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

const forgotPasswordRoutes: Route[] = [
  {
    path: '',
    component: ChangePasswordComponent
  }
];


@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(forgotPasswordRoutes),
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    SharedTranslateModule.forRoot(),
    SharedDirectivesTooltipModule,
    AuthDataAccessModule.forRoot(),
    SharedUiButtonModule,
    SharedUiInputModule,
    SharedUiSpinnerModule,
    SharedCommonTypoModule,
    SharedUiLayoutModule,
    SharedDirectivesTooltipModule,
    MatSlideToggleModule,
    MatPasswordStrengthModule
  ],
  declarations: [
    ChangePasswordComponent
  ],
  exports: [
    ChangePasswordComponent
  ]
})
export class BlockedAccountModule {}
