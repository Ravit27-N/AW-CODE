import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'cxm-smartflow-cxm-campaign',
  templateUrl: './cxm-campaign.component.html',
  styleUrls: ['./cxm-campaign.component.scss'],
})
export class CxmCampaignComponent {
  constructor(private translate: TranslateService) {
    this.translate.use(localStorage.getItem('locale') || 'fr' );
  }
}
