import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-common-button',
  templateUrl: './common-button.component.html',
  styleUrls: ['./common-button.component.scss']
})
export class CommonButtonComponent {
  @Input() type: 'Main'|'Alter' = 'Main';

  @Input() disabled: boolean | null = false;

  @Output() onclick = new EventEmitter();
}
