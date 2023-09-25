import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MatMenuTrigger } from '@angular/material/menu';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-template-filter-component',
  templateUrl: './template-filter-component.component.html',
  styleUrls: ['./template-filter-component.component.scss']
})
export class TemplateFilterComponentComponent implements OnInit, OnChanges {
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

  openMainMenu() {
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  closeMainMenu() {
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }

  filterValueChange(label: string, value: string, direction: string) {
    this._value = label;
    this.valueChanged.emit({
      label,
      sortByField: value,
      sortDirection: direction
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.value.isFirstChange()) {
      setTimeout(() => {
        this._value = 'template.sort_by.' + this.value;
      });
    }
  }

  patchValue(sortByField: string) {
    setTimeout(() => {
      this._value = 'template.sort_by.' + sortByField;
    });
  }

}
