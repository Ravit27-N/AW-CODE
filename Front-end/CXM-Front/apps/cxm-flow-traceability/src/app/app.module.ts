import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { CxmFlowTraceabilityModule } from './cxm-flow-traceability/cxm-flow-traceability.module';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule,
    BrowserAnimationsModule,
    CxmFlowTraceabilityModule,
    HttpClientModule,
    RouterModule.forRoot([
      {
        path: '',
        loadChildren: () =>
        import('./cxm-flow-traceability/cxm-flow-traceability.module').then(
          (m) => m.CxmFlowTraceabilityModule
        )
      },
      {
        path: 'cxm-flow-traceability',
        loadChildren: () =>
        import('./cxm-flow-traceability/cxm-flow-traceability.module').then(
          (m) => m.CxmFlowTraceabilityModule
        )
      }
    ])
  ],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  bootstrap: [AppComponent],
})
export class AppModule {}
