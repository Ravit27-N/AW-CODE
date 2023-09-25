import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    RouterModule.forRoot([
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'cxm-profile'
      },
      {
        path: 'cxm-profile',
        loadChildren: () => import('./cxm-profile/cxm-profile.module').then(
          (m) => m.CxmProfileModule
        )
      }
    ])
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
