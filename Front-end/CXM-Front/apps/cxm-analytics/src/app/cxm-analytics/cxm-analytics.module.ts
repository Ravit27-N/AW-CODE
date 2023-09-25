import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CxmAnalyticsComponent } from './cxm-analytics.component';
import { CxmAnalyticsRoutingModule } from './cxm-analytics-routing.module';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AuthDataAccessModule } from '@cxm-smartflow/auth/data-access';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import {
  DefaultRouterStateSerializer,
  StoreRouterConnectingModule,
} from '@ngrx/router-store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { WebpackTranslateLoader } from '@cxm-smartflow/shared/data-access/services';

@NgModule({
  declarations: [CxmAnalyticsComponent],
  imports: [
    CommonModule,
    AuthDataAccessModule.forRoot(),
    StoreModule.forRoot({}),
    EffectsModule.forRoot([]),
    StoreRouterConnectingModule.forRoot({
      serializer: DefaultRouterStateSerializer,
    }),
    StoreDevtoolsModule.instrument(),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useClass: WebpackTranslateLoader,
        deps: [HttpClient],
      },
      isolate: false,
    }),
    CxmAnalyticsRoutingModule,
  ],
})
export class CxmAnalyticsModule {}
