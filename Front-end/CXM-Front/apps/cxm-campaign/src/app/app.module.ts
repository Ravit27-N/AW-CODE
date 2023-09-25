import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { CxmCampaignModule } from './cxm-campaign/cxm-campaign.module';
import locales from '@angular/common/locales/fr';
import { registerLocaleData } from '@angular/common';

registerLocaleData(locales);

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    CxmCampaignModule,
    HttpClientModule,
    RouterModule.forRoot([
      {
        path: '',
        loadChildren: () =>
          import('./cxm-campaign/cxm-campaign.module').then(
            (m) => m.CxmCampaignModule
          )
      },
      // for run standalone
      {
        path: 'cxm-campaign',
        loadChildren: () =>
          import('./cxm-campaign/cxm-campaign.module').then(
            (m) => m.CxmCampaignModule
          )
      }
    ])
  ],
  providers: [],
  bootstrap: [AppComponent]
})

export class AppModule {
}
