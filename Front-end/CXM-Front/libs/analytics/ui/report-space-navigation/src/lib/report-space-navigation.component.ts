import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { appRoute } from '@cxm-smartflow/shared/data-access/model';

declare type NavigationType = 'global' | 'Postal' | 'Sms' | 'Email';

@Component({
  selector: 'cxm-smartflow-report-space-navigation',
  templateUrl: './report-space-navigation.component.html',
  styleUrls: ['./report-space-navigation.component.scss'],
})
export class ReportSpaceNavigationComponent {

  @Input() type: NavigationType;
  @Input() navigationVisible: NavigationType[] = [];

  constructor(private _router: Router) {}

  async navigateTo(type: NavigationType): Promise<void> {
    let URL = ``;
    switch (type) {
      case 'global': {
        URL = `${appRoute.cxmAnalytics.navigateToGlobal}`;
        break;
      }
      case 'Postal': {
        URL = `${appRoute.cxmAnalytics.navigateToPostal}`;
        break;
      }
      case 'Email': {
        URL = `${appRoute.cxmAnalytics.navigateToEmail}`;
        break;
      }
      case 'Sms': {
        URL = `${appRoute.cxmAnalytics.navigateToSms}`;
        break;
      }
    }

    await this._router.navigateByUrl(URL);
  }
}
