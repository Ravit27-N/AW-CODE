import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CheckListModel } from '@cxm-smartflow/shared/ui/dropdown-filter-criterial';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-multi-check',
  templateUrl: './multi-check.component.html',
  styleUrls: ['./multi-check.component.scss']
})
export class MultiCheckComponent implements OnChanges {
  @Input() subItems: CheckListModel [] = [];
  @Input() showTooltip = false;

  @Output() itemCheckEvent = new EventEmitter<string []>();

  itemsChecked = new BehaviorSubject<string []>([]);

  ngOnChanges(changes: SimpleChanges): void {
    // True, if subItems have change values.
    if (changes?.subItems) {
      this.itemsChecked.next([]);
    }
  }

  itemChecked(subItem: CheckListModel, checked: any) {
    subItem = JSON.parse(JSON.stringify(subItem));
    subItem.checked = !subItem.checked;
    if (checked && !this.itemsChecked.value.includes(subItem.value)) {
      this.itemsChecked.next([...this.itemsChecked.value, subItem.value]);
    } else {
      const finalSubItems = this.itemsChecked.value.filter((item: string) => item.toLowerCase() !== subItem.value.toLowerCase());
      this.itemsChecked.next(finalSubItems);
    }

    this.itemCheckEvent.emit(this.itemsChecked.value || []);
  }
}
