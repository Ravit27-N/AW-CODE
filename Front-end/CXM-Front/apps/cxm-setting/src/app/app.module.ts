import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { RouterModule } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    RouterModule.forRoot([
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'cxm-setting'
      },
      {
        path: 'cxm-setting',
        loadChildren: () => import('./cxm-setting/cxm-setting.module').then(
          (m) => m.CxmSettingModule
        )
      }
    ])
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
