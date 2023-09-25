import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'cxm-smartflow-cxm-profile',
  templateUrl: './cxm-profile.component.html',
  styleUrls: ['./cxm-profile.component.scss'],
})
export class CxmProfileComponent {
  constructor(private translate: TranslateService) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }
}
