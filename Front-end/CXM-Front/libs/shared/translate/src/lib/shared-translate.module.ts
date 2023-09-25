import { ModuleWithProviders, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  TranslateLoader,
  TranslateModule,
  TranslateService,
} from '@ngx-translate/core';
import { WebpackTranslateLoader } from '@cxm-smartflow/shared/data-access/services';
import { HttpClient } from '@angular/common/http';

@NgModule({
  imports: [
    CommonModule,
    TranslateModule.forChild({
      loader: {
        provide: TranslateLoader,
        useClass: WebpackTranslateLoader,
        deps: [HttpClient],
      },
      extend: true,
      isolate: true
    }),
  ],
  exports: [TranslateModule],
})
export class SharedTranslateModule {
  static forRoot(): ModuleWithProviders<SharedTranslateModule> {
    return {
      ngModule: SharedTranslateModule,
      providers: [TranslateService],
    };
  }

  constructor(private translate: TranslateService) {
    this.translate.setDefaultLang('fr');
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }
}
