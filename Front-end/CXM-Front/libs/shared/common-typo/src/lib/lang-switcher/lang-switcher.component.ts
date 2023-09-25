import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { TranslateConfigService } from '@cxm-smartflow/shared/data-access/services';

@Component({
  selector: 'cxm-smartflow-lang-switcher',
  templateUrl: './lang-switcher.component.html',
  styleUrls: ['./lang-switcher.component.scss']
})
export class LangSwitcherComponent implements OnInit {

  supportLangs = [
    { name: 'FR', lang: 'fr' },
    { name: 'EN', lang: 'en' }
  ];


  _language: string;


  switchTo(selected: any) {
    this._language = selected.name;
    this.langConfig.changeLocale(selected.lang)
  }

  constructor(private langConfig: TranslateConfigService, translate: TranslateService) { }

  ngOnInit(): void {
    const locale = localStorage.getItem('locale');

    if (!locale) {
      localStorage.setItem('locale', this.supportLangs[0].lang);
      this._language = this.supportLangs[0].name;
    } else {
      const l = this.supportLangs.find(x => x.lang === locale);
      this._language = l ? l.name : this.supportLangs[0].name;
    }

  }
}
