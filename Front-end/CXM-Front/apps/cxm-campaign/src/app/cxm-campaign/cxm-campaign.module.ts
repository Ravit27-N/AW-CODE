import { AuthDataAccessModule } from '@cxm-smartflow/auth/data-access';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CxmCampaignRoutingModule } from './cxm-campaign-routing.module';
import { CxmCampaignComponent } from './cxm-campaign/cxm-campaign.component';
import {
  DefaultRouterStateSerializer,
  StoreRouterConnectingModule,
} from '@ngrx/router-store';
import { WebpackTranslateLoader } from '@cxm-smartflow/shared/data-access/services';
import { HttpClient } from '@angular/common/http';
import {
  TranslateLoader,
  TranslateModule,
  TranslateService,
} from '@ngx-translate/core';

@NgModule({
  declarations: [CxmCampaignComponent],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    CxmCampaignRoutingModule,
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
  ],
  exports: [CxmCampaignComponent],
  providers: [TranslateService],
})
export class CxmCampaignModule {}
