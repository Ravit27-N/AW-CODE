import { Component, Input } from '@angular/core';

export interface RecipientModel {
  address?: string[],
  email?: string,
  telephone?: string,
  channel?: string,
  subChannel?: string
}

@Component({
  selector: 'cxm-smartflow-recipient',
  templateUrl: './recipient.component.html',
  styleUrls: ['./recipient.component.scss']
})
export class RecipientComponent {

  @Input() recipient: RecipientModel;

  getAddress(address: string, isBr?: boolean): string {
    const index = this.recipient.address?.indexOf(address) || -1;
    return (index + 1) === this.recipient.address?.length ? address : `${address} ${isBr? ',': ''}`;
  }

}
