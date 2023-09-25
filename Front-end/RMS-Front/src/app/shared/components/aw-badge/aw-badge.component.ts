import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-aw-badge',
  templateUrl: './aw-badge.component.html',
  styleUrls: ['./aw-badge.component.scss'],
})
export class AwBadgeComponent {
  @Input()
  color: 'primary' | 'secondary' | 'danger' | 'warning' | 'success' | 'purple' | 'blue' = 'primary';
}
