import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import {
  DefaultRouterStateSerializer,
  StoreRouterConnectingModule,
} from '@ngrx/router-store';
import { AuthDataAccessModule } from '@cxm-smartflow/auth/data-access';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CxmFlowTraceabilityRoutingModule } from './cxm-flow-traceability-routing.module';
import { CxmFlowTraceabilityComponent } from './cxm-flow-traceability.component';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import {
  TranslateLoader,
  TranslateModule,
  TranslateService,
} from '@ngx-translate/core';
import { WebpackTranslateLoader } from '@cxm-smartflow/shared/data-access/services';

@NgModule({
  declarations: [CxmFlowTraceabilityComponent],
  imports: [
    CommonModule,
    CxmFlowTraceabilityRoutingModule,
    AuthDataAccessModule.forRoot(),
    StoreModule.forRoot({}),
    EffectsModule.forRoot([]),
    StoreRouterConnectingModule.forRoot({
      serializer: DefaultRouterStateSerializer,
    }),
    StoreDevtoolsModule.instrument(),
    HttpClientModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useClass: WebpackTranslateLoader,
        deps: [HttpClient],
      },
      isolate: false,
    }),
  ],
  providers: [TranslateService],
})
export class CxmFlowTraceabilityModule {}
