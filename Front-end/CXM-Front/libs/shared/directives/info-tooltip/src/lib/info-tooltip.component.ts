import { Component } from '@angular/core';
import { animate, style, transition, trigger } from '@angular/animations';
import { SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'cxm-smartflow-info-tooltip',
  styleUrls: ['info-tooltip.component.scss'],
  template: `
    <div [ngClass]="[position, 'cxm-info-tooltip-container']" [ngStyle]='{width: width}'>
      <div [innerHTML]='tooltipMessage'></div>
        <cxm-smartflow-spinner *ngIf='showLoader'></cxm-smartflow-spinner>
    </div>
  `,
  animations: [
    trigger('tooltip', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate(300, style({ opacity: 1 })),
      ]),
      transition(':leave', [animate(300, style({ opacity: 0 }))]),
    ]),
  ],
})
export class InfoTooltipComponent {
  tooltipMessage: SafeHtml;
  width: string;
  showLoader: boolean;
  position = 'top';
}
