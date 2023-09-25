import { Component, Input } from '@angular/core';

export interface DeliverabilityRate {
  rates: {
    block: number,
    temporaryError: number,
    permanentError: number,
    dismissal: number,
    canceled: number,
    smsTotalError: number
  },
  totalMail: number,
  deliveredPercentage: number,
  totalDeliveredMail: number,
  type: 'email' | 'sms' | 'batch',
}

@Component({
  selector: 'cxm-smartflow-deliverability-rate',
  templateUrl: './deliverability-rate.component.html',
  styleUrls: ['./deliverability-rate.component.scss']
})
export class DeliverabilityRateComponent {

  isEnglishLocal = false;
  @Input() deliverabilityRate: DeliverabilityRate;

  constructor() {
    this.isEnglishLocal = localStorage.getItem('locale') === 'en';
  }
}
