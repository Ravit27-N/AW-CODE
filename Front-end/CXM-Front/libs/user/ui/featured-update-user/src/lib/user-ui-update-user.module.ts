import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UpdateUserComponent } from './update-user.component';
import { RouterModule } from '@angular/router';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { UserUiUserFormModule } from '@cxm-smartflow/user/ui/featured-user-form';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { MatPasswordStrengthModule } from '@angular-material-extensions/password-strength';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

@NgModule({
  imports: [
    CommonModule,
    UserUiUserFormModule,
    SharedCommonTypoModule,
    NgDynamicBreadcrumbModule,
    SharedUiButtonModule,
    RouterModule.forChild([
      {
        path: '',
        component: UpdateUserComponent,
      },
    ]),
    SharedTranslateModule.forRoot(),
    MatSlideToggleModule,
    MatPasswordStrengthModule
  ],
  declarations: [UpdateUserComponent],
})
export class UserUiUpdateUserModule {}
