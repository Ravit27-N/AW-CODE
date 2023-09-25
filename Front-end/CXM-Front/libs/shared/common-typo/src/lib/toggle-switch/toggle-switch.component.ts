import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-toggle-switch',
  templateUrl: './toggle-switch.component.html',
  styleUrls: ['./toggle-switch.component.scss']
})
export class ToggleSwitchComponent {

  toggleState = false;

  @Input() disabled = false

  @Output() onswitch = new EventEmitter<boolean>();

  toggle(state: boolean) {
    if(this.disabled) return;

    this.toggleState = state;
    this.onswitch.emit(this.toggleState);
  }
}
