import { Component, ChangeDetectionStrategy, Input } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-arch-progression',
  templateUrl: './arch-progression.component.html',
  styleUrls: ['./arch-progression.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ArchProgressionComponent {
  @Input() value = 0;
  @Input() label = '';
  @Input() counter = 0;
  @Input() color = 'rgb(0, 150, 136)';
}
