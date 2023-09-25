import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Route, RouterModule } from '@angular/router';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { AuthDataAccessModule } from '@cxm-smartflow/auth/data-access';
import { SharedUiInputModule } from '@cxm-smartflow/shared/ui/input';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import {
  FORGOT_PASSWORD_FEATURE_KEY,
  reducer,
} from './store/forgot-password.store';
import { ForgotPasswordEffect } from './store/forgot-password.effects';
import { ForgotPasswordQueryResolver } from './forgot-password.resolver';
import { ExpireLinkComponent } from './expire-link/expire-link.component';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedUiLayoutModule } from '@cxm-smartflow/shared/ui/layout';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

const forgotPasswordRoutes: Route[] = [
  {
    path: '',
    component: ForgotPasswordComponent,
    resolve: { mode: ForgotPasswordQueryResolver}
  },
  {
    path: 'expire-link',
    component: ExpireLinkComponent
  }
];


@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(forgotPasswordRoutes),
    FormsModule,
    StoreModule.forFeature(FORGOT_PASSWORD_FEATURE_KEY, reducer),
    EffectsModule.forFeature([ForgotPasswordEffect]),

    ReactiveFormsModule,
    MaterialModule,
    SharedTranslateModule.forRoot(),
    AuthDataAccessModule.forRoot(),
    SharedUiButtonModule,
    SharedUiInputModule,
    SharedUiSpinnerModule,
    SharedCommonTypoModule,
    SharedUiLayoutModule,
    SharedDirectivesTooltipModule
  ],
  declarations: [
    ForgotPasswordComponent,
    ExpireLinkComponent
  ],
})
export class ForgotPasswordModule {}
