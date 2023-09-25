import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { MatMenuTrigger } from '@angular/material/menu';


@Component({
  selector: 'cxm-smartflow-common-list-filter',
  templateUrl: './common-list-filter.component.html',
  styleUrls: ['./common-list-filter.component.scss']
})
export class CommonListFilterComponent implements OnInit, OnChanges {

  _value: string;

  @Input() value: string;
  @Output() valueChanged = new EventEmitter<{
    label: string;
    sortByField: string;
    sortDirection: string;
  }>();

  @ViewChild(MatMenuTrigger, { static: true }) menuTrigger: MatMenuTrigger;

  isSecondsLevel = false;

  ngOnInit(): void {
    this.menuTrigger.menuClosed.subscribe((_) => (this.isSecondsLevel = false));
  }

  openNameFilter($event: MouseEvent) {
    $event.stopPropagation();
    this.isSecondsLevel = !this.isSecondsLevel;
  }

  openMainMenu($event: MouseEvent) {
    $event.stopPropagation();
    // this.isSecondsLevel = !this.isSecondsLevel;
  }

  filterValueChange(label: string, value: string, direction: string) {
    this._value = label;
    this.valueChanged.emit({
      label,
      sortByField: value,
      sortDirection: direction,
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.value.isFirstChange()) {
      this._value = 'template.sort_by.' + this.value;
    }
  }

  patchValue(sortByField: string) {
    this._value = 'template.sort_by.' + sortByField;
  }
}
