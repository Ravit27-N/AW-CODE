import { RouterModule } from '@angular/router';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { ErrorHandler, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppComponent } from './app.component';
import { APP_ROUTES } from './app.route';
import { AuthDataAccessModule } from '@cxm-smartflow/auth/data-access';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';

// For Translation
import { HttpClient, HttpClientModule } from '@angular/common/http';
import {TranslateLoader, TranslateModule, TranslateService} from '@ngx-translate/core';

import locales from '@angular/common/locales/fr';
import { registerLocaleData } from '@angular/common';
import { AuthFeatureModule } from '@cxm-smartflow/auth/feature';
import { SharedDirectivesCanVisibilityModule } from '@cxm-smartflow/shared/directives/can-visibility';
import { CxmSmartflowComponent } from './cxm-smartflow/cxm-smartflow.component';
import { AboutComponent } from './about/about.component';
import { VersionComponent } from './about/version.component';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { GlobalErrorHandler } from './globa-error-handler';
import { ConfirmLogoutComponent } from './confirm-logout/confirm-logout.component';
import { SharedUiLayoutModule } from '@cxm-smartflow/shared/ui/layout';
import { IntegrationComponent } from './integration/integration.component';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { IntegrationFlowComponent } from './integration/integration-flow.component';
import { WebpackTranslateLoader } from '@cxm-smartflow/shared/data-access/services';
import { DashboardComponent } from './dashboard/dashboard.component';
import { SharedUiRouterLoadingModule } from '@cxm-smartflow/shared/ui/router-loading';

registerLocaleData(locales);

@NgModule({
  declarations: [
    AppComponent,
    CxmSmartflowComponent,
    AboutComponent, VersionComponent,
    ConfirmLogoutComponent,
    IntegrationComponent,
    IntegrationFlowComponent,
    DashboardComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    HttpClientModule,
    RouterModule.forRoot(APP_ROUTES),
    AuthDataAccessModule.forRoot(),
    StoreModule.forRoot([]),
    EffectsModule.forRoot([]),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useClass: WebpackTranslateLoader,
        deps: [HttpClient]
      },
      isolate: false
    }),
    StoreDevtoolsModule.instrument(),
    AuthFeatureModule,
    SharedDirectivesCanVisibilityModule,
    SharedCommonTypoModule,
    SharedUiLayoutModule,
    SharedUiSpinnerModule,
    SharedUiRouterLoadingModule
  ],
  providers: [{
    provide: ErrorHandler,
    useClass: GlobalErrorHandler
  }, TranslateService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
