import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { AppComponent } from './app.component';
import locales from '@angular/common/locales/fr';
import { registerLocaleData } from '@angular/common';

registerLocaleData(locales);
@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    RouterModule.forRoot([
      {
        path: '',
        loadChildren: () =>
          import('./cxm-template/cxm-template.module').then(
            (m) => m.CxmTemplateModule
          ),
      },
    ]),
  ],

  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
