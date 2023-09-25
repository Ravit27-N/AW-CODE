import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-aw-increment',
  templateUrl: './aw-increment.component.html',
  styleUrls: ['./aw-increment.component.scss'],
})
export class AwIncrementComponent {
  @Input() value = 0;
  @Input() disabled = false;
  @Output() addEvent = new EventEmitter<void>();

  add(): void {
    this.addEvent.emit();
  }
}
