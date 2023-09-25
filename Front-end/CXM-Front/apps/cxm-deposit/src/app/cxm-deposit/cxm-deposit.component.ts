import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'cxm-smartflow-cxm-deposit',
  templateUrl: './cxm-deposit.component.html',
  styleUrls: ['./cxm-deposit.component.scss']
})
export class CxmDepositComponent {

  constructor(private translate: TranslateService) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }
}
