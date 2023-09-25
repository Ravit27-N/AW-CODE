import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-aw-slider-toggle',
  templateUrl: './aw-slider-toggle.component.html',
})
export class AwSliderToggleComponent {
  @Input() disabled = false;
  @Input() checked = false;
  @Output() valueChangeEvent = new EventEmitter<boolean>();

  toggle() {
    if (this.disabled) {
      return;
    }

    this.valueChangeEvent.emit(this.checked);
  }
}
