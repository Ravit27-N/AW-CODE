import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-header-button',
  templateUrl: './header-button.component.html',
  styleUrls: ['./header-button.component.scss']
})
export class HeaderButtonComponent {
  @Output() onclick = new EventEmitter();
  @Input() icon: string;

  @Input() width = "";
  @Input() height = "48px";
  @Input() minWidth = "242px";
  @Input() padding = "";
}
