import { Component, Input, TemplateRef } from '@angular/core';
import { TooltipComponent } from '@angular/material/tooltip';

@Component({
  selector: 'app-aw-tooltip',
  templateUrl: './aw-tooltip.component.html',
  styleUrls: ['./aw-tooltip.component.scss'],
})
export class AwTooltipComponent extends TooltipComponent {
  @Input() text: string;
  @Input() contentTemplate: TemplateRef<any>;
}
