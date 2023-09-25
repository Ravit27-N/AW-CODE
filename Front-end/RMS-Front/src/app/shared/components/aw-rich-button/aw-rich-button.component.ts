import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-aw-rich-button',
  templateUrl: './aw-rich-button.component.html',
})
export class AwRichButtonComponent {
  @Input() icon: string;
  @Input() type: 'primary' | 'danger' | 'warning' | 'success' = 'primary';
  @Input() height = '48px';
  @Input() width = '285px';
  @Input() padding = '';
  @Input() outline = false;
  @Input() disabled = false;
  @Input() actionType: 'submit' | 'button' = 'button';
  @Input() borderRadius = '25px';
}
