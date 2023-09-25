import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CxmDepositRoutingModule } from './cxm-deposit-routing.module';
import { CxmDepositComponent } from './cxm-deposit.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AuthDataAccessModule } from '@cxm-smartflow/auth/data-access';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import {
  DefaultRouterStateSerializer,
  StoreRouterConnectingModule,
} from '@ngrx/router-store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { WebpackTranslateLoader } from '@cxm-smartflow/shared/data-access/services';


@NgModule({
  declarations: [
    CxmDepositComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    CxmDepositRoutingModule,
    AuthDataAccessModule.forRoot(),
    StoreModule.forRoot({}),
    EffectsModule.forRoot([]),
    StoreRouterConnectingModule.forRoot({
      serializer: DefaultRouterStateSerializer
    }),
    StoreDevtoolsModule.instrument(),
    HttpClientModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useClass: WebpackTranslateLoader,
        deps: [HttpClient]
      },
      isolate: false
    })
  ],
  exports: [
    CxmDepositComponent
  ]
})
export class CxmDepositModule {
}
