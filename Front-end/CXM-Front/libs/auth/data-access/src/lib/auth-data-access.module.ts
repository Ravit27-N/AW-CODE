import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { ModuleWithProviders, NgModule } from '@angular/core';
import { campaignEnv } from '@env-cxm-campaign';
import { templateEnv } from '@env-cxm-template';
import { flowTraceabilityEnv } from '@env-flow-traceability';
import { flowDepositEnv } from '@env-flow-deposit';
import { OAuthModule } from 'angular-oauth2-oidc';
import { AuthGuard } from './guard/auth.guard';
import { AuthService } from './services/auth.service';
import { LoginGuard } from './guard/login.guard';
import { ExtAuthService } from './services/auth-ext.service';

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule,
    OAuthModule.forRoot({
      resourceServer: {
        // allowedUrls: [
          // campaignEnv.apiURL,
          // templateEnv.apiURL,
          // flowTraceabilityEnv.apiURL,
          // flowDepositEnv.apiURL
        // ],
        sendAccessToken: true
      }
    })
  ]
})
export class AuthDataAccessModule {
  static forRoot(): ModuleWithProviders<AuthDataAccessModule> {
    return {
      providers: [AuthGuard, AuthService, LoginGuard, ExtAuthService],
      ngModule: AuthDataAccessModule
    };
  }
}
