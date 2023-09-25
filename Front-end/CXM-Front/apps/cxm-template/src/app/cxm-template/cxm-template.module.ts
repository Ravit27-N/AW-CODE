import { AuthDataAccessModule } from '@cxm-smartflow/auth/data-access';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { StoreModule } from '@ngrx/store';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CxmTemplateRoutingModule } from './cxm-template-routing.module';
import { CxmTemplateComponent } from './cxm-template/cxm-template.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { WebpackTranslateLoader } from '@cxm-smartflow/shared/data-access/services';
import { HttpClient } from '@angular/common/http';

@NgModule({
  declarations: [CxmTemplateComponent],
  imports: [
    CommonModule,
    StoreModule.forRoot({}),
    EffectsModule.forRoot(),
    StoreDevtoolsModule.instrument(),
    CxmTemplateRoutingModule,
    AuthDataAccessModule.forRoot(),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useClass: WebpackTranslateLoader,
        deps: [HttpClient],
      },
      isolate: false,
    }),
  ],
  exports: [CxmTemplateComponent],
})
export class CxmTemplateModule {}
