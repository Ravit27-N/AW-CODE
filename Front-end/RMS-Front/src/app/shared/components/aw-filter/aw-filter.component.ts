import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { CustomMaterialUiUtil } from '../../utils';
import { KeyValue } from '@angular/common';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-aw-filter',
  templateUrl: './aw-filter.component.html',
  styleUrls: ['./aw-filter.component.scss'],
})
export class AwFilterComponent implements OnInit, OnChanges, OnDestroy {
  @Input() active = false;
  @Output() valueChange = new EventEmitter<{
    filter: string;
    status: string;
    useFilter: boolean;
  }>();
  filterFormGroup: FormGroup;
  @Input() filterItems: Array<KeyValue<string, string>> = [];
  @Input() filterGroup: Array<KeyValue<string, string>> = [];
  @Input() selectedGroups: string[] = [];
  @Input() selectedItems: string = '';

  #subscription = new Subscription();

  constructor(private fb: FormBuilder) {
    this.filterFormGroup = this.fb.group({});
  }

  get status() {
    return this.filterItems.filter(
      (value) => value.key === this.filterFormGroup.get('status')?.value,
    )[0]?.value;
  }

  get filter() {
    return this.filterGroup
      .filter((keyValue) => this.filterFormGroup.get(`${keyValue.key}`)?.value)
      .map((value: KeyValue<string, string>) => value.key)
      .map((value: string) => value);
  }

  ngOnInit(): void {
    if (this.filterFormGroup) {
      this.filterFormGroup = this.fb.group({});
      this.filterFormGroup?.addControl('filter', new FormControl(''));
      this.filterFormGroup?.addControl('status', new FormControl(''));
      this.filterGroup.forEach((keyValue: KeyValue<string, string>) => {
        this.filterFormGroup?.addControl(keyValue.key, new FormControl(false));
      });
    }
    const subscription = this.filterFormGroup.valueChanges.subscribe(() =>
      this.filterChange(),
    );
    this.setSelectedGroups();
    this.setSelectedItems();
    this.#subscription.add(subscription);
  }

  ngOnChanges() {
    this.setSelectedGroups();
    this.setSelectedItems();
  }

  ngOnDestroy(): void {
    this.#subscription.unsubscribe();
  }

  setSelectedGroups(): void {
    this.filterGroup.forEach((group) => {
      this.filterFormGroup
        ?.get(`${group.key}`)
        ?.patchValue(this.selectedGroups.includes(group.key), {
          emitEvent: false,
        });
      if (this.selectedGroups.includes(group.key)) {
        this.active = true;
      }
    });
  }

  setSelectedItems(): void {
    const selectedItemKey = this.filterItems
      .filter(
        (selectedItem: KeyValue<string, string>) =>
          this.selectedItems === selectedItem.key,
      )
      .map((selectedItem: KeyValue<string, string>) => selectedItem.key)
      .map((key) => key)
      .toString();
    this.filterFormGroup
      ?.get('status')
      ?.patchValue(selectedItemKey, { emitEvent: false });
    if (selectedItemKey.length) {
      this.active = true;
    }
  }

  filterChange(): void {
    const hasFilters = Object.keys(this.filterFormGroup.controls)
      .map((k) => this.filterFormGroup.controls[k])
      .some((x) => x.value || x.value?.length);
    this.active = hasFilters;
    this.valueChange.emit({
      filter: this.filter?.toString(),
      status: this.status,
      useFilter: hasFilters,
    });
  }

  resetForm(): void {
    this.filterFormGroup.get('status').setValue('');
    this.filterFormGroup.reset();
    this.active = false;
  }

  menuOpen(): void {
    CustomMaterialUiUtil.decreaseCdkOverlayContainerZIndex();
  }

  menuClose(): void {
    CustomMaterialUiUtil.increaseCdkOverlayContainerZIndex();
  }
}
