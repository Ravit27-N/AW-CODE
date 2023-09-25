import { Component, Input } from '@angular/core';
import { AttachmentDetail } from '@cxm-smartflow/flow-deposit/data-access';

@Component({
  selector: 'cxm-smartflow-info-tooltip',
  template: `<img
    (mouseover)="enterIcon(enrichmentElement)"
    (mouseout)="leaveIcon(enrichmentDetail.originalName, enrichmentElement)"
    *ngIf="enrichmentDetail.missing"
    src="assets/icons/alert-red.png"
    [ngStyle]="getStyle()"
    alt="infoTooltip"
    class="info-icon h-4 w-4 default-image-style"
    cxmSmartflowCxmTooltip
    [isRemoveBorder]="true"
    [X]="-20"
    [showTooltip]="true"
    [tooltipText]="'background.buttons.missingRes' | translate"
  />`,
})
export class InfoTooltipComponent {
  @Input() enrichmentElement: HTMLParagraphElement;
  @Input() enrichmentDetail: AttachmentDetail;

  addTitle(
    originalName: string,
    paragraphElement: HTMLParagraphElement
  ): string {
    return paragraphElement.clientWidth < paragraphElement.scrollWidth
      ? originalName
      : '';
  }

  enterIcon(paragraphElement: HTMLParagraphElement): void {
    paragraphElement.removeAttribute('title');
  }

  leaveIcon(
    originalName: string,
    paragraphElement: HTMLParagraphElement
  ): void {
    paragraphElement.setAttribute(
      'title',
      this.addTitle(originalName, paragraphElement)
    );
  }

  getStyle(): any {
    if (window.screen.width >= 1765 && window.screen.width <= 2064) {
      return {'margin-left': '5px', 'margin-top':' 5px'};
    }
    return {'margin-left': '5px', 'margin-top': '5px', 'margin-right': '5px'};
  }
}
