import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthConfig, OAuthModule, OAuthStorage } from 'angular-oauth2-oidc';
import { AuthGuardService } from './auth-guard.service';
import { pkceflow } from './pkce';
import { NotificationService } from './notification.service';
import { NotificationSubscriptionService } from '../core';
import { CanAccessDirective } from './can-access.directive';
import { AccessGuardService } from './access-guard.service';
import { environment } from 'src/environments/environment';

export const storageFactory = (): OAuthStorage => localStorage;

@NgModule({
  declarations: [CanAccessDirective],
  imports: [
    CommonModule,
    OAuthModule.forRoot({
      resourceServer: {
        sendAccessToken: true,
        allowedUrls: [`${environment.apiUrl}`]
      }
    })
  ],
  exports: [
    CanAccessDirective
  ],
  providers: [
    NotificationSubscriptionService,
    NotificationService,
    AuthGuardService,
    AccessGuardService,
    { provide: AuthConfig, useValue: pkceflow },
    { provide: OAuthStorage, useFactory: storageFactory },
  ]
})
export class AuthModule { }
