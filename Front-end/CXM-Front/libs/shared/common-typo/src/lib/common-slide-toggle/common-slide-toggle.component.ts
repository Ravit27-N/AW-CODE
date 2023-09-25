import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';

@Component({
  selector: 'cxm-smartflow-common-slide-toggle',
  templateUrl: './common-slide-toggle.component.html',
  styleUrls: ['./common-slide-toggle.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default,
})
export class CommonSlideToggleComponent {
  @Input() disabled = false;
  @Input() toggleChecked = false;

  @Output() slideToggle = new EventEmitter<boolean>();

  toggle() {
    if (this.disabled) return;

    this.slideToggle.emit(this.toggleChecked);
  }
}
