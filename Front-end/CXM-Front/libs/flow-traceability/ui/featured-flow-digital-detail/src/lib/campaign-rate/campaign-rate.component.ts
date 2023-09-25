import { Component, Input } from '@angular/core';

export interface CampaignRate {
  rates: {
    open: {
      messages: string,
      percentage: number
    },
    clicked: {
      messages: string,
      percentage: number
    },
    error: {
      messages: string,
      percentage: number
    },
    bound: {
      messages: string,
      percentage: number
    },
    delivered?: {
      messages: string,
      percentage: number
    },
    cancel?: {
      messages: string,
      percentage: number
    }
  },
  htmlTemplate: string,
  type: 'sms' | 'email' | 'batch',
  exportFileButtonVisible: boolean,
}

@Component({
  selector: 'cxm-smartflow-campaign-rate',
  templateUrl: './campaign-rate.component.html',
  styleUrls: ['./campaign-rate.component.scss']
})
export class CampaignRateComponent {

  isPopupVisible = false;
  @Input() campaignRate: CampaignRate;

  togglePopup(): void {
    this.isPopupVisible = !this.isPopupVisible;
  }
}
