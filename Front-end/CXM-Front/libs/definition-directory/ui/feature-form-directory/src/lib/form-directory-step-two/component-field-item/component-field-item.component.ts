import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'cxm-smartflow-component-field-item',
  templateUrl: './component-field-item.component.html',
  styleUrls: ['./component-field-item.component.scss'],
})
export class ComponentFieldItemComponent implements OnInit {

  @Input() fieldItemTitle = '';
  @Input() fieldItemSelected = false;
  @Input() fieldItemPrimary = false;
  @Input() fieldItemDeletable = false;
  @Input() fieldItemDraggable = false;
  @Output() fieldItemDeleteEvent = new EventEmitter<boolean>();
  selected = false;

  constructor() {}

  ngOnInit(): void {}

  triggerDeleteEvent(): void {
    this.fieldItemDeleteEvent.emit(true);
  }

  addSelectionItem() {
    this.selected = true;
  }

  removeSelectionItem() {
    this.selected = this.fieldItemSelected;
  }
}
