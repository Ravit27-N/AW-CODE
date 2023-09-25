import { Component, Input, TemplateRef } from '@angular/core';
import { TooltipComponent } from '@angular/material/tooltip';

@Component({
  selector: 'cxm-smartflow-cxm-custom-tooltip',
  templateUrl: './cxm-custom-tooltip.component.html',
  styleUrls: ['./cxm-custom-tooltip.component.scss']
})
export class CxmCustomTooltipComponent extends TooltipComponent {

  @Input() text?: string;
  @Input() contentTemplate: TemplateRef<any>;
}
