import { Component } from '@angular/core';
import { animate, style, transition, trigger } from '@angular/animations';
import { SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'app-aw-tooltip-info',
  templateUrl: './aw-tooltip-info.component.html',
  styleUrls: ['./aw-tooltip-info.component.scss'],
  animations: [
    trigger('tooltip', [
      transition(':enter', [
        style({ transform: 'scale(0.8)', opacity: 0 }),
        animate('200ms ease-in', style({ transform: 'scale(1)', opacity: 1 })),
      ]),
      transition(':leave', [
        animate(
          '200ms ease-out',
          style({ transform: 'scale(0.8)', opacity: 0 }),
        ),
      ]),
    ]),
  ],
})
export class AwTooltipInfoComponent {
  tooltipMessage: SafeHtml;
  width: string;
  showLoader: boolean;
  position = 'top';
}
