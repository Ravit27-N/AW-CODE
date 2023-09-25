import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-aw-card',
  templateUrl: './aw-card.component.html',
  styleUrls: ['./aw-card.component.scss'],
})
export class AwCardComponent {
  @Input() icon = 'calendar_month';
  @Input() type:
    | 'primary'
    | 'success'
    | 'warning'
    | 'danger'
    | 'secondary'
    | 'passed'
    | 'failed' = 'primary';
  @Input() primaryText = '';
  @Input() secondaryText = '';
  @Input() thirdText = '';
}
