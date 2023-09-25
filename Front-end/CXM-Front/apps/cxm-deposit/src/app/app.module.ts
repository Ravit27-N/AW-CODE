import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { RouterModule } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CxmDepositModule } from './cxm-deposit/cxm-deposit.module';
import { HttpClientModule } from '@angular/common/http';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    CxmDepositModule,
    HttpClientModule,
    TranslateModule,
    RouterModule.forRoot([
      {
        path: '',
        loadChildren: () =>
          import('./cxm-deposit/cxm-deposit.module').then(
            (m) => m.CxmDepositModule
          )
      },
      // Run standalone
      {
        path: 'cxm-deposit',
        pathMatch: 'full',
        loadChildren: () =>
          import('./cxm-deposit/cxm-deposit.module').then(
            (m) => m.CxmDepositModule
          )
      }
    ]),
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
