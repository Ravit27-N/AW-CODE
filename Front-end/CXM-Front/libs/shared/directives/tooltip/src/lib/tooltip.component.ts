import { Component, Input } from "@angular/core";


@Component({
  selector: 'cxm-smartflow-tooltip',
  styleUrls: ['tooltip.component.scss'],
  template: `
    <span class="cxm-tooltip-content text-sm inline-block"
    [class.mode-success]="this.mode === 'success'"
    [class.mode-error]="this.mode === 'error'"
    >
      <mat-icon *ngIf="this.mode === 'error'" [inline]="true" style="font-size: 12px;">warning</mat-icon>
      {{ text }}
  </span>
  `
})
export class TooltipComponent {
  @Input() text = '';
  @Input() mode: 'success' | 'error' | 'rich';
}


@Component({
  selector: 'cxm-smartflow-richtooltip',
  styleUrls: ['tooltip.component.scss'],
  template: `
    <div class="text-sm inline-block rich-tooltip ">
      <div class="px-6 p-2" [innerHtml]="text|safeHtml"></div>
    </div>
  `
})
export class RichTooltipComponent {
  @Input() text = '';
  @Input() mode: 'success' | 'error'| 'rich';
}
