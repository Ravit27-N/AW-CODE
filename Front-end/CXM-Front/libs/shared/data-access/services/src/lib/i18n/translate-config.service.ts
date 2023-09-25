import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

export function rootLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, 'assets/i18n/', '.json');
}

@Injectable({
  providedIn: 'any',
})
export class TranslateConfigService {
  // localeEvent = new Subject<string>();

  constructor(private translate: TranslateService) {}

  changeLocale(locale: string) {
    this.translate.use(locale);
    // this.localeEvent.next(locale);
    localStorage.setItem('locale', locale);
    window.location.reload();
  }
}
