import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  AUTH_FEATURE_KEY,
  AuthDataAccessModule,
  AuthEffects,
  authReducer,
  LoginGuard,
  AuthExtEffect
} from '@cxm-smartflow/auth/data-access';

import { SharedUiInputModule } from '@cxm-smartflow/shared/ui/input';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { AuthUiLoginModule } from '@cxm-smartflow/auth/ui/login';
import { AuthComponent } from './auth/auth.component';

@NgModule({
  imports: [
    CommonModule,
    SharedUiInputModule,
    AuthDataAccessModule.forRoot(),
    StoreModule.forFeature(AUTH_FEATURE_KEY, authReducer),
    EffectsModule.forFeature([AuthEffects,  AuthExtEffect]),
    StoreDevtoolsModule.instrument(),
    AuthUiLoginModule,
    RouterModule.forChild([
      {
        path: '',
        canActivate: [LoginGuard],
        component: AuthComponent
      }
    ])
  ],
  declarations: [AuthComponent],
  exports: [AuthComponent]
})

export class AuthFeatureModule {
}
