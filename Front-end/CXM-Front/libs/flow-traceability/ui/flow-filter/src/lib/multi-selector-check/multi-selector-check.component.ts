import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { CheckedListKeyValue } from '@cxm-smartflow/flow-traceability/data-access';

@Component({
  selector: 'cxm-smartflow-multi-selector-check',
  templateUrl: './multi-selector-check.component.html',
  styleUrls: ['./multi-selector-check.component.scss'],
})
export class MultiSelectorCheckComponent implements OnChanges {
  @Input() items: CheckedListKeyValue[] = [];
  @Input() isShowTooltip = false;

  itemsChecked: string[] = [];
  currentItemCheck: any;
  _userSelected: string[] = [];

  @Output() checkedItems = new EventEmitter<string[]>();
  @Output() individualCheckedItems = new EventEmitter<any>();
  @Output() userSelectedItems = new EventEmitter<string[]>();

  onCheckedItems(value: string, checked?: boolean, other?: string) {
    this.itemsChecked = Object.assign([], this.itemsChecked);
    this._userSelected = Object.assign([], this._userSelected);

    if (checked) {
      this.itemsChecked.push(value);
      if (other) {
        this._userSelected.push(other);
      }
    } else {
      const index = this.itemsChecked.indexOf(value);
      this.itemsChecked.splice(index, 1);
      if (other) {
        const index = this._userSelected.indexOf(other);
        this._userSelected.splice(index, 1);
      }
    }

    this._userSelected = this.items.filter(x => x.checked===true).map(y => y.other);

    this.currentItemCheck = { value: value, checked: checked, other: other };

    this.onEmitCheckedItems();
    this.onEmitCurrentCheckedItems();
    this.onEmitUsersDropdown();
  }

  onEmitCheckedItems(): void {
    this.checkedItems.emit(this.itemsChecked);
  }

  onEmitUsersDropdown(): void {
    this.userSelectedItems.emit(this._userSelected);
  }

  onEmitCurrentCheckedItems(): void {
    this.individualCheckedItems.emit(this.currentItemCheck);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.itemsChecked.length > 0) {
      this.checkedItems.emit([]);
      this.individualCheckedItems.emit({});
      this.userSelectedItems.emit([]);
    }

    this.itemsChecked = [];
    this.currentItemCheck = {};
  }
}
