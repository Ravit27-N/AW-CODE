import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Route, RouterModule } from '@angular/router';
import { CreateUserComponent } from './create-user.component';
import { HttpClientModule } from '@angular/common/http';
import { UserUiUserFormModule } from '@cxm-smartflow/user/ui/featured-user-form';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { MatPasswordStrengthModule } from '@angular-material-extensions/password-strength';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

export const userUiCreateUserRoutes: Route[] = [
  {
    path: '',
    component: CreateUserComponent,
  },
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    SharedCommonTypoModule,
    RouterModule.forChild(userUiCreateUserRoutes),
    HttpClientModule,
    UserUiUserFormModule,
    NgDynamicBreadcrumbModule,
    SharedTranslateModule.forRoot(),
    MatSlideToggleModule,
    MatPasswordStrengthModule
  ],
  declarations: [CreateUserComponent],
})
export class UserUiCreateUserModule {}
